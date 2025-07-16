package com.frogus.drinkordie.temperature;


import com.frogus.drinkordie.data.BalanceTemperatureConfig;
import com.frogus.drinkordie.temperature.PlayerTemperature;
import com.frogus.drinkordie.temperature.PlayerTemperatureProvider;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;


public class TemperatureCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("temperature")
                        .requires(source -> source.hasPermission(0)) // OP Only
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();


                            // Aktuelle Temperatur (Capability)
                            LazyOptional<PlayerTemperature> tempCap = player.getCapability(PlayerTemperatureProvider.TEMPERATURE_CAP);
                            float current = tempCap.map(PlayerTemperature::getTemperature).orElse(Float.NaN);

                            // Berechne Zieltemperatur wie im Event
                            float targetTemperature = BalanceTemperatureConfig.DATA.normal;

                            float biomeTemp = player.level().getBiome(player.blockPosition()).value().getBaseTemperature();
                            float biomeDelta = (biomeTemp - 1.0f) * BalanceTemperatureConfig.DATA.biomeTemperatureMultiplier;
                            targetTemperature += biomeDelta;

                            if (player.isSprinting()) targetTemperature += BalanceTemperatureConfig.DATA.sprintBoost;
                            if (player.getDeltaMovement().horizontalDistanceSqr() > 0.002 && !player.isPassenger())
                                targetTemperature += BalanceTemperatureConfig.DATA.walkBoost;
                            if (player.isOnFire()) targetTemperature += BalanceTemperatureConfig.DATA.fireBoost;
                            if (player.level().getBlockState(player.blockPosition().below()).is(net.minecraft.world.level.block.Blocks.LAVA)) {
                                targetTemperature += BalanceTemperatureConfig.DATA.lavaBoost;
                            }
                            if (player.getY() > 64) targetTemperature -= (player.getY() - 64) * BalanceTemperatureConfig.DATA.altitudeColdFactor;
                            if (player.getY() < 64) targetTemperature += (64 - player.getY()) * BalanceTemperatureConfig.DATA.caveWarmFactor;

                            Level world = player.level();
                            long time = world.getDayTime() % 24000;
                            boolean isDay = (time > 0 && time < 12000);

                            if (isDay) {
                                targetTemperature += BalanceTemperatureConfig.DATA.daytimeBonus;
                            } else {
                                targetTemperature -= BalanceTemperatureConfig.DATA.nighttimePenalty;
                            }

                            int eyeX = (int) player.getX();
                            int eyeY = (int) player.getEyeY();
                            int eyeZ = (int) player.getZ();
                            BlockPos eyePos = new BlockPos(eyeX, eyeY, eyeZ);
                            boolean inSunlight = isDay && world.canSeeSky(eyePos) && !world.isRainingAt(eyePos);

                            if (inSunlight) {
                                targetTemperature += BalanceTemperatureConfig.DATA.directSunlightBonus;
                            }

                            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                    String.format("Current temperature: %.2f°C\nTarget temperature: %.2f°C", current, targetTemperature)
                            ));

                            return Command.SINGLE_SUCCESS;
                        })
        );
    }
}
