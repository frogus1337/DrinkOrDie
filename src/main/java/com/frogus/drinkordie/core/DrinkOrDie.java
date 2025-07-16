package com.frogus.drinkordie.core;

import com.frogus.drinkordie.network.DrinkOrDieNetwork;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(DrinkOrDie.MODID)
public class DrinkOrDie {
    public static final String MODID = "drinkordie";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DrinkOrDie() {
        // Netzwerk-Registrierung
        DrinkOrDieNetwork.register();

        LOGGER.info("Drink Or Die loaded!");
    }
}
