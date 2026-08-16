package com.dex.autovault;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public class AutoVaultConfig {
    public boolean enabled = true;
    public boolean openOminousVaults = true;
    public boolean openNormalVaults = true;
    public boolean itemFilter = true;
    public boolean trident = true;
    public boolean mace = true;
    public boolean heavyCore = true;
    public boolean enchantedBook = true;
    public boolean windBurstOnly = true;
    public Set<String> customItems = new LinkedHashSet<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static AutoVaultConfig load(Path path) {
        try {
            if (Files.exists(path)) {
                try (Reader reader = Files.newBufferedReader(path)) {
                    AutoVaultConfig config = GSON.fromJson(reader, AutoVaultConfig.class);
                    if (config != null) {
                        if (config.customItems == null) config.customItems = new LinkedHashSet<>();
                        return config;
                    }
                }
            }
        } catch (Exception ignored) {}
        return new AutoVaultConfig();
    }

    public void save(Path path) {
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(this, writer);
            }
        } catch (Exception ignored) {}
    }
}
