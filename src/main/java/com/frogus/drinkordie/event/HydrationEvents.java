package com.frogus.drinkordie.event;
import com.frogus.drinkordie.hydration.PlayerHydration;
import com.frogus.drinkordie.hydration.PlayerHydrationProvider;
import com.frogus.drinkordie.sync.SyncHydrationPacket;
import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.network.DrinkOrDieNetwork;
import net.minecraft.world.entity.player.Player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HydrationEvents {

    private static final WeakHashMap<UUID, Float> hydrationRest = new WeakHashMap<>();
    private static final java.util.WeakHashMap<java.util.UUID, Boolean> wasOnGround = new java.util.WeakHashMap<>();

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
            // Spieler war vorher auf dem Boden, ist jetzt in der Luft = SPRUNG!
            jumped = true;
        }
        wasOnGround.put(uuid, onGround);

        if (jumped) {
            LazyOptional<PlayerHydration> hydrationCap = player.getCapability(PlayerHydrationProvider.HYDRATION_CAP);
            hydrationCap.ifPresent(hydration -> {
                hydration.setHydration(hydration.getHydration() - 0.1f); // z.B. 0.2 pro Sprung
                // Sync an Client
                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    DrinkOrDieNetwork.INSTANCE.send(
                            net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                            new SyncHydrationPacket(hydration.getHydration())
                    );
                }
            });
        }


        LazyOptional<PlayerHydration> hydrationCap = player.getCapability(PlayerHydrationProvider.HYDRATION_CAP);

        hydrationCap.ifPresent(hydration -> {
            // -------- Hydration-Verbrauch pro Tick --------
            float hydrationLoss = 0.1f / 24f; // Standard (stehen)

            if (player.isSprinting()) {
                hydrationLoss = 0.1f / 12f; // Sprint: doppelt so schnell
            } else if (player.getDeltaMovement().horizontalDistanceSqr() > 0.001 && !player.isPassenger()) {
                hydrationLoss = 0.1f / 16f; // Gehen: 1,5x so schnell
            }

            float rest = hydrationRest.getOrDefault(uuid, 0.0f) + hydrationLoss;

            if (rest >= 0.1f) {
                int times = (int)(rest / 0.1f);
                float toSubtract = 0.1f * times;
                hydration.setHydration(hydration.getHydration() - toSubtract);

                // Nach jedem Abzug zum Client syncen!
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

            // Unter 25: Effekte
            if (hydrationValue < 25f) {
                int slowness = 0;
                int weakness = 0;
                int miningFatigue = -1;
                float damage = 0f;
                int fatigueTicks = 60;
                int weaknessTicks = 60;
                int slownessTicks = 60;

                if (hydrationValue <= 0f) {
                    slowness = 5; // Stufe 4
                    weakness = 3; // Stufe 4
                    miningFatigue = 3; // Stufe 4
                    fatigueTicks = 60;
                    weaknessTicks = 60;
                    slownessTicks = 60;
                    damage = 4.0f; // 2 Herzen
                    // Death by dehydration - DeathMessage (optional)
                    if (player.getHealth() - damage <= 0.0f && !player.getTags().contains("dehydration_death")) {
                        player.addTag("dehydration_death");
                    }
                    if (player.tickCount % 20 == 0) {
                        player.hurt(player.damageSources().starve(), damage);
                    }
                } else if (hydrationValue < 5f) {
                    slowness = 4; // Stufe 2
                    weakness = 2; // Stufe 3
                    miningFatigue = 1; // Stufe 2
                    fatigueTicks = 60;
                    weaknessTicks = 60;
                    slownessTicks = 60;
                    damage = 2.0f; // 1 Herz alle 3 Sekunden
                    if (player.tickCount % 60 == 0) {
                        player.hurt(player.damageSources().starve(), damage);
                    }
                } else if (hydrationValue < 10f) {
                    slowness = 2; // Stufe 2
                    weakness = 1; // Stufe 2
                    miningFatigue = 0; // Stufe 1
                } else if (hydrationValue < 25f) {
                    slowness = 0; // Stufe 1
                    weakness = 0; // Stufe 1
                }




                if (slowness >= 0) player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, slownessTicks, slowness, true, true));
                if (weakness >= 0) player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, weaknessTicks, weakness, true, true));
                if (miningFatigue >= 0) player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, fatigueTicks, miningFatigue, true, true));
            }

            // "You feel dizzy..."-Message ab unter 10 Hydration (alle 6 Sekunden)
            if (hydrationValue < 10f && player.tickCount % 120 == 0) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("You feel dizzy..."));
            }

            // Custom-Death-Message vorbereiten (optional, siehe unten)
            // (Die eigentliche Anzeige erfolgt in einer Event-Handler für LivingDeathEvent, falls gewünscht.)
        });
    }
}
