package com.frogus.drinkordie.data;

import com.google.gson.Gson;
import java.io.FileReader;
import java.nio.file.Path;

public class BalanceTemperatureConfig {
    public static BalanceTemperatureData DATA = new BalanceTemperatureData();

    public static void loadJsonConfig(Path configDir) {
        Gson gson = new Gson();
        try {
            Path file = configDir.resolve("BalanceTemperature.json");
            FileReader reader = new FileReader(file.toFile());
            DATA = gson.fromJson(reader, BalanceTemperatureData.class);
            reader.close();
            System.out.println("[DrinkOrDie] TemperatureBalanceConfig loaded!");
        } catch (Exception e) {
            System.err.println("[DrinkOrDie] Could not load TemperatureBalanceConfig: " + e.getMessage());
        }
    }
}
