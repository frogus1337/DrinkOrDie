package com.frogus.drinkordie.data;

import com.google.gson.Gson;

import java.io.FileReader;
import java.nio.file.Path;

public class BalanceHydrationConfig {
    public static BalanceData DATA = new BalanceData();

    public static void loadJsonConfig(Path configDir) {
        Gson gson = new Gson();
        try {
            Path file = configDir.resolve("BalanceHydration.json");
            FileReader reader = new FileReader(file.toFile());
            DATA = gson.fromJson(reader, BalanceData.class);
            reader.close();
            System.out.println("[DrinkOrDie] BalanceHydration loaded!");
        } catch (Exception e) {
            System.err.println("[DrinkOrDie] Could not load HydrationBalanceConfig: " + e.getMessage());
        }
    }
}
