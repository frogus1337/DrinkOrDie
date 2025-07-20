package com.frogus.drinkordie.core;

import com.frogus.drinkordie.data.BalanceHydrationConfig;
import com.frogus.drinkordie.data.BalanceTemperatureConfig;
import com.frogus.drinkordie.data.DataMap;
import com.frogus.drinkordie.effect.ModEffects;
import com.frogus.drinkordie.network.DrinkOrDieNetwork;
import com.frogus.drinkordie.temperature.TemperatureCommand;
import com.frogus.drinkordie.item.ModItems;
import com.frogus.drinkordie.fluid.ModFluids;
import com.frogus.drinkordie.fluid.ModFluidTypes;
import com.frogus.drinkordie.block.ModBlocks;
import com.mojang.logging.LogUtils;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(DrinkOrDie.MODID)
public class DrinkOrDie {
    public static final String MODID = "drinkordie";
    private static final Logger LOGGER = LogUtils.getLogger();

    public DrinkOrDie() {
        // Holt den ModEventBus für alle Registry-Aufrufe
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 1. Registriere alles am EventBus
        ModFluidTypes.register(modEventBus); // FluidTypes
        ModFluids.register(modEventBus);// Fluids

        ModItems.register(modEventBus);      // Items
        ModBlocks.register(modEventBus);     // Blöcke

        ModEffects.register(modEventBus);


        // 2. Netzwerk
        DrinkOrDieNetwork.register();

        // 3. Konfigs laden
        DataMap.loadJsonConfig(net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get());
        BalanceHydrationConfig.loadJsonConfig(net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get());
        BalanceTemperatureConfig.loadJsonConfig(net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get());

        // 4. Commands
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) -> {
            DrinkOrDieGeneralCommands.register(event.getDispatcher());
            TemperatureCommand.register(event.getDispatcher());
        });

        LOGGER.info("Drink Or Die loaded!");
    }
}
