package com.dex.autovault;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class AutoVaultConfigScreen extends Screen {
    private TextFieldWidget itemField;
    private final List<String> customSnapshot = new ArrayList<>();

    public AutoVaultConfigScreen() {
        super(Text.literal("AutoVault Configuration"));
    }

    @Override
    protected void init() {
        AutoVaultConfig cfg = AutoVaultClient.getConfig();
        int left = this.width / 2 - 155;
        int y = 45;

        addDrawableChild(ButtonWidget.builder(toggleText("Normal Vaults", cfg.openNormalVaults),
                b -> { cfg.openNormalVaults = !cfg.openNormalVaults; b.setMessage(toggleText("Normal Vaults", cfg.openNormalVaults)); })
                .dimensions(left, y, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(toggleText("Ominous Vaults", cfg.openOminousVaults),
                b -> { cfg.openOminousVaults = !cfg.openOminousVaults; b.setMessage(toggleText("Ominous Vaults", cfg.openOminousVaults)); })
                .dimensions(left + 160, y, 150, 20).build());

        y += 26;
        addDrawableChild(ButtonWidget.builder(toggleText("Item Filter", cfg.itemFilter),
                b -> { cfg.itemFilter = !cfg.itemFilter; b.setMessage(toggleText("Item Filter", cfg.itemFilter)); })
                .dimensions(left, y, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(toggleText("Wind Burst only", cfg.windBurstOnly),
                b -> { cfg.windBurstOnly = !cfg.windBurstOnly; b.setMessage(toggleText("Wind Burst only", cfg.windBurstOnly)); })
                .dimensions(left + 160, y, 150, 20).build());

        y += 30;
        addDrawableChild(ButtonWidget.builder(toggleText("Trident", cfg.trident),
                b -> { cfg.trident = !cfg.trident; b.setMessage(toggleText("Trident", cfg.trident)); })
                .dimensions(left, y, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(toggleText("Mace", cfg.mace),
                b -> { cfg.mace = !cfg.mace; b.setMessage(toggleText("Mace", cfg.mace)); })
                .dimensions(left + 105, y, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(toggleText("Heavy Core", cfg.heavyCore),
                b -> { cfg.heavyCore = !cfg.heavyCore; b.setMessage(toggleText("Heavy Core", cfg.heavyCore)); })
                .dimensions(left + 210, y, 100, 20).build());

        y += 30;
        addDrawableChild(ButtonWidget.builder(toggleText("Enchanted Book", cfg.enchantedBook),
                b -> { cfg.enchantedBook = !cfg.enchantedBook; b.setMessage(toggleText("Enchanted Book", cfg.enchantedBook)); })
                .dimensions(left, y, 150, 20).build());

        y += 30;
        itemField = new TextFieldWidget(this.textRenderer, left, y, 220, 20, Text.literal("minecraft:diamond"));
        itemField.setMaxLength(120);
        itemField.setPlaceholder(Text.literal("minecraft:diamond"));
        addDrawableChild(itemField);

        addDrawableChild(ButtonWidget.builder(Text.literal("Add"),
                b -> addCustomItem()).dimensions(left + 225, y, 85, 20).build());

        y += 25;
        addDrawableChild(ButtonWidget.builder(Text.literal("Remove"),
                b -> removeCustomItem()).dimensions(left + 225, y, 85, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Save & Close"),
                b -> { AutoVaultClient.saveConfig(); close(); })
                .dimensions(left, this.height - 35, 150, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"),
                b -> close())
                .dimensions(left + 160, this.height - 35, 150, 20).build());
    }

    private Text toggleText(String name, boolean value) {
        return Text.literal(name + ": " + (value ? "ON" : "OFF"));
    }

    private void addCustomItem() {
        String id = itemField.getText().trim();
        if (!id.isEmpty() && id.contains(":")) {
            AutoVaultClient.getConfig().customItems.add(id);
            itemField.setText("");
        }
    }

    private void removeCustomItem() {
        String id = itemField.getText().trim();
        if (!id.isEmpty()) {
            AutoVaultClient.getConfig().customItems.remove(id);
            itemField.setText("");
        }
    }

    @Override
    public void close() {
        AutoVaultClient.saveConfig();
        MinecraftClient.getInstance().setScreen(null);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 18, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer,
                Text.literal("Custom items: " + AutoVaultClient.getConfig().customItems.size()),
                this.width / 2 - 155, this.height - 58, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }
}
