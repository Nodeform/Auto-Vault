package com.dex.autovault;

import net.minecraft.client.util.InputUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VaultBlock;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultSharedData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;

public class AutoVaultClient implements ClientModInitializer {
    public static final String MOD_ID = "autovault";
    private static final Path CONFIG_PATH = Path.of("config", "autovault.json");

    private static AutoVaultConfig config;
    private static KeyBinding toggleKey;
    private static KeyBinding configKey;
    private static BlockPos lastTarget;
    private static int cooldownTicks = 0;
    private static String lastDisplayed = "";

    @Override
    public void onInitializeClient() {
        config = AutoVaultConfig.load(CONFIG_PATH);

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autovault.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.autovault"
        ));
        configKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autovault.config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.autovault"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(AutoVaultClient::tick);
    }

    private static void tick(MinecraftClient client) {
        while (toggleKey.wasPressed()) {
            config.enabled = !config.enabled;
            saveConfig();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("AutoVault: " + (config.enabled ? "ON" : "OFF")), true);
            }
        }

        while (configKey.wasPressed()) {
            client.setScreen(new AutoVaultConfigScreen());
        }

        if (client.player == null || client.world == null || client.currentScreen != null) return;

        if (cooldownTicks > 0) cooldownTicks--;

        if (!(client.crosshairTarget instanceof BlockHitResult hit)) {
            clearDisplay(client.player);
            lastTarget = null;
            return;
        }

        BlockPos pos = hit.getBlockPos();
        BlockState state = client.world.getBlockState(pos);
        if (!state.isOf(Blocks.VAULT)) {
            clearDisplay(client.player);
            lastTarget = null;
            return;
        }

        VaultBlockEntity vault = client.world.getBlockEntity(pos) instanceof VaultBlockEntity v ? v : null;
        if (vault == null) {
            clearDisplay(client.player);
            return;
        }

        boolean ominous = state.get(VaultBlock.OMINOUS);
        ItemStack display = vault.getSharedData().getDisplayItem();

        if (!display.isEmpty()) {
            String displayName = display.getName().getString();
            if (!displayName.equals(lastDisplayed) || !pos.equals(lastTarget)) {
                client.player.sendMessage(Text.literal("Vault: " + displayName), true);
                lastDisplayed = displayName;
            }
        } else {
            clearDisplay(client.player);
        }

        lastTarget = pos;

        if (!config.enabled || cooldownTicks > 0) return;
        if (ominous && !config.openOminousVaults) return;
        if (!ominous && !config.openNormalVaults) return;
        if (display.isEmpty()) return;
        if (config.itemFilter && !isWanted(display)) return;

        ItemStack keyTemplate = vault.getConfig().keyItem();
        if (keyTemplate == null || keyTemplate.isEmpty()) return;

        int keySlot = findKeySlot(client.player, keyTemplate.getItem());
        if (keySlot < 0) return;

        int oldSlot = client.player.getInventory().getSelectedSlot();
        if (oldSlot != keySlot) {
            client.player.getInventory().setSelectedSlot(keySlot);
        }

        client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hit);
        cooldownTicks = 8;

        // Restore the previous slot after the interaction packet is sent.
        if (oldSlot != keySlot) {
            client.player.getInventory().setSelectedSlot(oldSlot);
        }
    }

    private static void clearDisplay(ClientPlayerEntity player) {
        if (!lastDisplayed.isEmpty()) {
            player.sendMessage(Text.empty(), true);
            lastDisplayed = "";
        }
    }

    private static int findKeySlot(ClientPlayerEntity player, Item item) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).isOf(item)) return i;
        }
        return -1;
    }

    private static boolean isWanted(ItemStack stack) {
        String id = Registries.ITEM.getId(stack.getItem()).toString();

        if (id.equals("minecraft:trident") && config.trident) return true;
        if (id.equals("minecraft:mace") && config.mace) return true;
        if (id.equals("minecraft:heavy_core") && config.heavyCore) return true;

        if (id.equals("minecraft:enchanted_book") && config.enchantedBook) {
            return !config.windBurstOnly || hasWindBurst(stack);
        }

        return config.customItems.contains(id);
    }

    private static boolean hasWindBurst(ItemStack stack) {
        ItemEnchantmentsComponent stored = stack.get(DataComponentTypes.STORED_ENCHANTMENTS);
        if (stored == null || stored.isEmpty()) return false;

        var registry = MinecraftClient.getInstance().world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
        RegistryEntry<net.minecraft.enchantment.Enchantment> windBurst = registry.getOrThrow(Enchantments.WIND_BURST);
        return stored.getLevel(windBurst) > 0;
    }

    public static AutoVaultConfig getConfig() {
        return config;
    }

    public static void saveConfig() {
        if (config != null) config.save(CONFIG_PATH);
    }
}
