package com.frogus.drinkordie.core;

import com.frogus.drinkordie.data.BalanceHydrationConfig;
import com.frogus.drinkordie.data.BalanceTemperatureConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.loading.FMLPaths;

public class DrinkOrDieGeneralCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("drinkordie_reload")
                        .requires(source -> source.hasPermission(2)) // Permission 2 = Operator/Admin
                        .executes(ctx -> {
                            // Hier werden die JSON-Configs neu geladen:
                            BalanceHydrationConfig.loadJsonConfig(FMLPaths.CONFIGDIR.get());
                            BalanceTemperatureConfig.loadJsonConfig(FMLPaths.CONFIGDIR.get());

                            ctx.getSource().sendSuccess(() ->
                                    Component.literal("DrinkOrDie configs reloaded!"), true);
                            return Command.SINGLE_SUCCESS;
                        })
        );
    }
}
