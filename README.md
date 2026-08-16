# AutoVault — Minecraft Fabric 1.21.11

Client-side Fabric mod that targets the Vault currently under the crosshair, reads the Vault's client-visible display item, filters it, selects the correct Vault key from the hotbar, and performs the normal block interaction.

## Controls

- **G** — toggle AutoVault
- **K** — open configuration

## Default whitelist

- Trident
- Mace
- Heavy Core
- Enchanted Book

Enchanted Books require **Wind Burst** by default. This can be disabled in the GUI.

## Config

Saved at:

`.minecraft/config/autovault.json`

Custom item IDs use normal registry IDs such as `minecraft:diamond`.

## Build

Requires Java 21.

1. Open this folder as a Gradle project in IntelliJ IDEA.
2. Refresh Gradle.
3. Run `./gradlew build` on macOS/Linux or `gradlew.bat build` on Windows.
4. Put `build/libs/autovault-1.0.0.jar` into your Fabric client's `mods` folder.
5. Also install Fabric API for 1.21.11.

### Wrapper note

The source project includes the Gradle wrapper configuration. If your checkout does not contain `gradle-wrapper.jar`, run `gradle wrapper --gradle-version 8.13` once with a local Gradle installation, or use IntelliJ's Gradle import to generate the wrapper.

## Important implementation note

This is intentionally client-side. It does **not** bypass server-side Vault rules: the server still decides whether the key is valid and whether the player can unlock the Vault. The mod only automates the normal client interaction and prevents that interaction when the currently displayed item does not pass the configured filter.

Minecraft 1.21.11 is the final obfuscated release before the 26.1 unobfuscation change, so this project uses Yarn mappings for 1.21.11. Fabric's documentation confirms that 1.21.11 is the last obfuscated version and that Yarn/Intermediary stop after it.

### Key handling

The mod uses the exact `keyItem` configured by the focused Vault and looks for that key in hotbar slots 1–9. It does not move items between inventory slots.
