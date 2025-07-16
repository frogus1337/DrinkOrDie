package com.frogus.drinkordie.data;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.FileReader;

public class DataMap {
    public static final Map<String, HydrationData> HYDRATION_MAP = new HashMap<>();
    public static final HydrationData DEFAULT = new HydrationData(0f, 0f);

    // --> ACHTUNG: public static!
    public static void loadJsonConfig(Path configDir) {
        Gson gson = new Gson();
        try {
            Path file = configDir.resolve("DrinkOrDieConfig.json");
            FileReader reader = new FileReader(file.toFile());
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            for (String key : json.keySet()) {
                JsonObject entry = json.getAsJsonObject(key);
                float hydration = entry.get("hydration").getAsFloat();
                float temperature = entry.get("temperature").getAsFloat();
                HYDRATION_MAP.put(key, new HydrationData(hydration, temperature));
            }
            reader.close();
            System.out.println("[DrinkOrDie] DrinkOrDieConfig.json loaded: " + HYDRATION_MAP.size() + " entries");
        } catch (Exception e) {
            System.err.println("[DrinkOrDie] Could not load DrinkOrDieConfig.json: " + e.getMessage());
        }
    }

    public static HydrationData getForItem(String itemId) {
        return HYDRATION_MAP.getOrDefault(itemId, DEFAULT);
    }
}
