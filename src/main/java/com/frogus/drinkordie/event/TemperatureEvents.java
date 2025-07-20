package com.frogus.drinkordie.event;

import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.data.BalanceTemperatureConfig;
import com.frogus.drinkordie.temperature.PlayerTemperature;
import com.frogus.drinkordie.temperature.PlayerTemperatureProvider;
import com.frogus.drinkordie.hydration.PlayerHydration;
import com.frogus.drinkordie.hydration.PlayerHydrationProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID)
public class TemperatureEvents {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(
                    ResourceLocation.fromNamespaceAndPath(DrinkOrDie.MODID, "temperature"),
                    new PlayerTemperatureProvider()
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;

        LazyOptional<PlayerTemperature> tempCap = player.getCapability(PlayerTemperatureProvider.TEMPERATURE_CAP);
        tempCap.ifPresent(temp -> {
            float value = temp.getTemperature();

            // --- Zieltemperatur bestimmen ---
            float targetTemperature = BalanceTemperatureConfig.DATA.normal;

            // Biome-Temperatur (dynamisch, mit Offset und Multiplikator aus JSON)
            float biomeTemp = player.level().getBiome(player.blockPosition()).value().getBaseTemperature();
            float biomeDelta = (biomeTemp - 1.0f) * BalanceTemperatureConfig.DATA.biomeTemperatureMultiplier;
            targetTemperature += biomeDelta;

            // Aktivitäten (aus JSON)
            if (player.isSprinting()) targetTemperature += BalanceTemperatureConfig.DATA.sprintBoost;
            if (player.getDeltaMovement().horizontalDistanceSqr() > 0.002 && !player.isPassenger())
                targetTemperature += BalanceTemperatureConfig.DATA.walkBoost;
            if (player.isOnFire()) targetTemperature += BalanceTemperatureConfig.DATA.fireBoost;

            // Lava-Nähe
            if (player.level().getBlockState(player.blockPosition().below()).is(net.minecraft.world.level.block.Blocks.LAVA)) {
                targetTemperature += BalanceTemperatureConfig.DATA.lavaBoost;
            }

            // Höhe (Berg oder Höhle)
            if (player.getY() > 64) targetTemperature -= (player.getY() - 64) * BalanceTemperatureConfig.DATA.altitudeColdFactor;
            if (player.getY() < 64) targetTemperature += (64 - player.getY()) * BalanceTemperatureConfig.DATA.caveWarmFactor;

            // Tageszeit-Effekte
            Level world = player.level();
            long time = world.getDayTime() % 24000;
            boolean isDay = (time > 0 && time < 12000);

            if (isDay) {
                targetTemperature += BalanceTemperatureConfig.DATA.daytimeBonus;
            } else {
                targetTemperature -= BalanceTemperatureConfig.DATA.nighttimePenalty;
            }

            // Direkte Sonne (direkt sichtbarer Himmel, kein Dach, Tag)
            BlockPos eyePos = new BlockPos(
                    (int) player.getX(),
                    (int) player.getEyeY(),
                    (int) player.getZ()
            );
            boolean inSunlight = isDay && world.canSeeSky(eyePos) && !world.isRainingAt(eyePos);
            if (inSunlight) {
                targetTemperature += BalanceTemperatureConfig.DATA.directSunlightBonus;
            }

            // --- Hydration beeinflusst Temperatur-Ziel ---
            float hydration = player.getCapability(PlayerHydrationProvider.HYDRATION_CAP)
                    .map(PlayerHydration::getHydration)
                    .orElse(100f);
            if (hydration < 50f) targetTemperature += BalanceTemperatureConfig.DATA.hydrationPenaltyBelow50;
            if (hydration < 10f) targetTemperature += BalanceTemperatureConfig.DATA.hydrationPenaltyBelow10;

            // --- Temperatur proportional angleichen ---
            float diff = targetTemperature - value;
            double extra = 1;
            if (diff >= 2) extra = 1.2;
            if (diff >= 3) extra = 1.9;
            if (diff >= 4) extra = 2.2;
            value += diff * (BalanceTemperatureConfig.DATA.adjustFactor * extra);

            // Clamp
            value = Math.max(BalanceTemperatureConfig.DATA.min, Math.min(BalanceTemperatureConfig.DATA.max, value));
            temp.setTemperature(value);

            // Temperatur nach jedem Tick an den Client syncen:
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                com.frogus.drinkordie.network.DrinkOrDieNetwork.INSTANCE.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new com.frogus.drinkordie.sync.SyncTemperaturePacket(value)
                );
            }
            //System.out.println("[TEMP] Thresholds: " + (BalanceTemperatureConfig.DATA.effectThresholds != null ? BalanceTemperatureConfig.DATA.effectThresholds.size() : "null"));
            if (BalanceTemperatureConfig.DATA.effectThresholds != null) {
                for (com.frogus.drinkordie.data.BalanceTemperatureData.EffectThreshold t : BalanceTemperatureConfig.DATA.effectThresholds) {
                    //System.out.println("[TEMP] Schwelle geladen: min=" + t.min + " max=" + t.max + " eff=" + t.effect);
                }
            }

            // --- Effekte aus effectThresholds anwenden (NEU, mit min & max) ---
            if (BalanceTemperatureConfig.DATA.effectThresholds != null) {
                for (com.frogus.drinkordie.data.BalanceTemperatureData.EffectThreshold t : BalanceTemperatureConfig.DATA.effectThresholds) {
                    // Bereich trifft zu?
                    if (value >= t.min && value < t.max) {
                        // Slowness
                        if (t.slowness >= 0)
                            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, t.slowness, true, true));
                        // Weakness
                        if (t.weakness >= 0)
                            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, t.weakness, true, true));
                        // Nausea/Confusion
                        if (t.nausea >= 0)
                            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, t.nausea, true, true));
                        // Schaden
                        if (t.damage > 0 && player.tickCount % 60 == 0) {
                            player.hurt(player.damageSources().starve(), t.damage);
                        }
                        //System.out.println("[TEMP-EFFECT] Applied: " + t.effect + " for temp " + value);

                        break; // Nur einen Bereich pro Tick!
                    }
                }
            }
        });
    }
}
