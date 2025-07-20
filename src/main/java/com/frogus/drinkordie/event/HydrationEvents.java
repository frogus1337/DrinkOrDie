package com.frogus.drinkordie.event;

import com.frogus.drinkordie.hydration.PlayerHydrationProvider;
import com.frogus.drinkordie.sync.SyncHydrationPacket;
import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.network.DrinkOrDieNetwork;
import com.frogus.drinkordie.data.BalanceHydrationConfig;
import com.frogus.drinkordie.data.BalanceData;
import com.frogus.drinkordie.temperature.PlayerTemperature;
import com.frogus.drinkordie.temperature.PlayerTemperatureProvider;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HydrationEvents {

    private static final WeakHashMap<UUID, Float> hydrationRest = new WeakHashMap<>();
    private static final WeakHashMap<UUID, Boolean> wasOnGround = new WeakHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;

        Player player = event.player;
        UUID uuid = player.getUUID();
        if (player.isCreative() || player.isSpectator()) return;

        // --- Sprung-Erkennung ---
        boolean onGround = player.onGround();
        boolean jumped = false;
        if (wasOnGround.getOrDefault(uuid, true) && !onGround) {
            jumped = true;
        }
        wasOnGround.put(uuid, onGround);

        if (jumped) {
            player.getCapability(PlayerHydrationProvider.HYDRATION_CAP).ifPresent(hydration -> {
                hydration.setHydration(hydration.getHydration() - BalanceHydrationConfig.DATA.jumpLoss);
                if (player instanceof ServerPlayer serverPlayer) {
                    DrinkOrDieNetwork.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new SyncHydrationPacket(hydration.getHydration())
                    );
                }
            });
        }

        player.getCapability(PlayerHydrationProvider.HYDRATION_CAP).ifPresent(hydration -> {
            // -------- Temperaturbonus holen --------
            float temperature = player.getCapability(PlayerTemperatureProvider.TEMPERATURE_CAP)
                    .map(PlayerTemperature::getTemperature)
                    .orElse(36.5f);

            float baseLoss = BalanceHydrationConfig.DATA.standingLossPerTick / 24f;
            if (player.isSprinting()) {
                baseLoss = BalanceHydrationConfig.DATA.sprintingLossPerTick / 12f;
            } else if (player.getDeltaMovement().horizontalDistanceSqr() > 0.001 && !player.isPassenger()) {
                baseLoss = BalanceHydrationConfig.DATA.walkingLossPerTick / 16f;
            }

            // --- Temperatur-Bonus berechnen: z.B. +5% pro Grad über 36.5°C ---
            float tempBonus = Math.max(0f, temperature - 36.5f) * BalanceHydrationConfig.DATA.temperatureHydrationMultiplier;
            float totalHydrationLoss = baseLoss * (1.0f + tempBonus);

            float rest = hydrationRest.getOrDefault(uuid, 0.0f) + totalHydrationLoss;

            if (rest >= 0.1f) {
                int times = (int)(rest / 0.1f);
                float toSubtract = 0.1f * times;
                hydration.setHydration(hydration.getHydration() - toSubtract);

                if (player instanceof ServerPlayer serverPlayer) {
                    DrinkOrDieNetwork.INSTANCE.send(
                            PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new SyncHydrationPacket(hydration.getHydration())
                    );
                }
                rest = rest - toSubtract;
            }
            hydrationRest.put(uuid, rest);

            // -------- Effekte und Schaden je nach Wert --------
            float hydrationValue = hydration.getHydration();

            if (BalanceHydrationConfig.DATA.effectThresholds != null) {
                for (BalanceData.EffectThreshold t : BalanceHydrationConfig.DATA.effectThresholds) {
                    if (hydrationValue >= t.min && hydrationValue < t.max) {
                        if (t.slowness >= 0)
                            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, t.slowness, true, true));
                        if (t.weakness >= 0)
                            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, t.weakness, true, true));
                        if (t.miningFatigue >= 0)
                            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, t.miningFatigue, true, true));
                        if (t.damage > 0 && t.damageCooldown > 0 && player.tickCount % t.damageCooldown == 0) {
                            player.hurt(player.damageSources().starve(), t.damage);
                        }
                        break;
                    }
                }
            }

            // "You feel dizzy..."-Message ab unter 10 Hydration (alle 6 Sekunden)
            if (hydrationValue < 10f && player.tickCount % 120 == 0) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("You feel dizzy..."));
            }
        });
    }
}
