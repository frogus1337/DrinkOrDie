package com.frogus.drinkordie.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HeatSourceManager {
    public static List<HeatSourceConfig> HEAT_SOURCES = new ArrayList<>();

    // Call this once at startup or reload!
    public static void loadConfig() {
        try {
            Path configFile = FMLPaths.CONFIGDIR.get().resolve("HeatSourceConfig.json");
            Gson gson = new Gson();
            FileReader reader = new FileReader(configFile.toFile());
            HEAT_SOURCES = gson.fromJson(reader, new TypeToken<List<HeatSourceConfig>>(){}.getType());
            reader.close();
            if (HEAT_SOURCES == null) HEAT_SOURCES = new ArrayList<>();
            System.out.println("[DrinkOrDie] HeatSourceConfig loaded! Found " + HEAT_SOURCES.size() + " entries.");
        } catch (Exception e) {
            System.err.println("[DrinkOrDie] Could not load HeatSourceConfig.json: " + e.getMessage());
            HEAT_SOURCES = new ArrayList<>();
        }
    }

    public static boolean isHeatBlock(ResourceLocation blockId) {
        for (HeatSourceConfig cfg : HEAT_SOURCES) {
            if (blockId.toString().equals(cfg.block)) return true;
        }
        return false;
    }

    public static HeatSourceConfig getConfig(ResourceLocation blockId) {
        for (HeatSourceConfig cfg : HEAT_SOURCES) {
            if (blockId.toString().equals(cfg.block)) return cfg;
        }
        return null;
    }
}
