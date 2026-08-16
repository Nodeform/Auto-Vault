# 1.21.11 compatibility notes

- Minecraft: 1.21.11
- Yarn: 1.21.11+build.4
- Fabric Loader: 0.18.4
- Fabric API: 0.141.4+1.21.11
- Java: 21
- Loom: 1.14-SNAPSHOT

The Vault API used by the mod is the 1.21.11 API:
- `VaultBlock.OMINOUS`
- `VaultBlockEntity#getSharedData()`
- `VaultSharedData#getDisplayItem()`
- `VaultConfig#keyItem()`
- `DataComponentTypes.STORED_ENCHANTMENTS`
- `ItemEnchantmentsComponent#getLevel(...)`

The crosshair target comes from Minecraft's normal client raycast, so it does not search for Vaults through walls or extend block reach.
