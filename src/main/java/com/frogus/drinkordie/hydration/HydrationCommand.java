package com.frogus.drinkordie.hydration;

import com.frogus.drinkordie.sync.SyncHydrationPacket;
import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.network.DrinkOrDieNetwork;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID)
public class HydrationCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(



                Commands.literal("hydration")
                        // /hydration zeigt Wert an
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            Player player = source.getPlayerOrException();

                            LazyOptional<PlayerHydration> hydrationCap = player.getCapability(PlayerHydrationProvider.HYDRATION_CAP);
                            hydrationCap.ifPresent(hydration -> {
                                player.sendSystemMessage(
                                        net.minecraft.network.chat.Component.literal(
                                                "Your hydration: " + hydration.getHydration() + " / 100"
                                        )
                                );
                            });
                            return Command.SINGLE_SUCCESS;
                        })
                        // /hydration set <wert>
                        .then(
                                Commands.literal("set")
                                        .then(
                                                Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                                        .executes(context -> {
                                                            CommandSourceStack source = context.getSource();
                                                            Player player = source.getPlayerOrException();
                                                            int value = IntegerArgumentType.getInteger(context, "value");

                                                            LazyOptional<PlayerHydration> hydrationCap = player.getCapability(PlayerHydrationProvider.HYDRATION_CAP);
                                                            hydrationCap.ifPresent(hydration -> {
                                                                hydration.setHydration(value);
                                                                // Synchronisiere nach Änderung
                                                                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                                                                    DrinkOrDieNetwork.INSTANCE.send(
                                                                            PacketDistributor.PLAYER.with(() -> serverPlayer),
                                                                            new SyncHydrationPacket(value)
                                                                    );
                                                                }
                                                                player.sendSystemMessage(
                                                                        net.minecraft.network.chat.Component.literal(
                                                                                "Your hydration: " + String.format("%.1f", hydration.getHydration()) + " / 100"
                                                                        )
                                                                );
                                                            });
                                                            return Command.SINGLE_SUCCESS;
                                                        })
                                        )
                        )
        );
    }
}
