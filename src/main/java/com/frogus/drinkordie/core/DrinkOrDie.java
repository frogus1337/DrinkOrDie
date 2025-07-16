package com.frogus.drinkordie.core;

import com.frogus.drinkordie.data.BalanceHydrationConfig;
import com.frogus.drinkordie.network.DrinkOrDieNetwork;
import com.frogus.drinkordie.temperature.TemperatureCommand;  // <--- Importiere deinen Command!
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import net.minecraftforge.event.RegisterCommandsEvent;

@Mod(DrinkOrDie.MODID)
public class DrinkOrDie {
    public static final String MODID = "drinkordie";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DrinkOrDie() {
        // Netzwerk-Registrierung
        DrinkOrDieNetwork.register();

        LOGGER.info("Drink Or Die loaded!");

        com.frogus.drinkordie.data.DataMap.loadJsonConfig(net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get());
        BalanceHydrationConfig.loadJsonConfig(net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get());

        // Command-Registrierung: Lambda direkt im Konstruktor!
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.addListener(
                (RegisterCommandsEvent event) -> TemperatureCommand.register(event.getDispatcher())
        );
    }
}
