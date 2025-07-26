package com.frogus.drinkordie.event;

import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.data.BalanceTemperatureConfig;
import com.frogus.drinkordie.data.HeatSourceConfig;
import com.frogus.drinkordie.data.HeatSourceManager;
import com.frogus.drinkordie.temperature.PlayerTemperature;
import com.frogus.drinkordie.temperature.PlayerTemperatureProvider;
import com.frogus.drinkordie.hydration.PlayerHydration;
import com.frogus.drinkordie.hydration.PlayerHydrationProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID)
public class TemperatureEvents {

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

            // Lava-Nähe (legacy-Boost, optional lassen)
            if (player.level().getBlockState(player.blockPosition().below()).is(net.minecraft.world.level.block.Blocks.LAVA)) {
                targetTemperature += BalanceTemperatureConfig.DATA.lavaBoost;
            }

            // ---- HeatSourceManager: Offene Heat Blocks (Raytraced + Property-Check!) ----
            float heatBoost = 0f;
            Vec3 playerEye = player.getEyePosition(1.0f);

            for (HeatSourceConfig cfg : HeatSourceManager.HEAT_SOURCES) {
                int r = cfg.range;
                String[] parts = cfg.block.split(":", 2);
                if (parts.length != 2) continue;
                ResourceLocation blockId = ResourceLocation.fromNamespaceAndPath(parts[0], parts[1]);
                Block blockType = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(blockId);
                if (blockType == null) continue;

                BlockPos playerPos = player.blockPosition();
                for (BlockPos pos : BlockPos.betweenClosed(playerPos.offset(-r, -r, -r), playerPos.offset(r, r, r))) {
                    BlockState state = player.level().getBlockState(pos);
                    if (state.getBlock() == blockType) {
                        // Property-Check (z.B. "lit": true)
                        boolean propertyOk = true;
                        if (cfg.property != null) {
                            for (Map.Entry<String, Object> entry : cfg.property.entrySet()) {
                                String key = entry.getKey();
                                Object val = entry.getValue();
                                // Beispiel für "lit"
                                if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) && key.equals("lit")) {
                                    if (state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) != ((Boolean) val)) {
                                        propertyOk = false;
                                        break;
                                    }
                                }
                                // Optional: weitere Properties ergänzen, z.B. facing, age, etc.
                            }
                        }
                        if (!propertyOk) continue;

                        // Raytrace vom Auge zum Blockzentrum
                        Vec3 blockCenter = Vec3.atCenterOf(pos);
                        HitResult result = player.level().clip(new ClipContext(
                                playerEye, blockCenter,
                                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player
                        ));
                        if (result.getType() == HitResult.Type.MISS ||
                                (result.getType() == HitResult.Type.BLOCK &&
                                        ((BlockHitResult) result).getBlockPos().equals(pos))) {
                            heatBoost += cfg.temperatureBoost;
                        }
                    }
                }
            }
            targetTemperature += heatBoost;

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

            // --- Effekte aus effectThresholds anwenden (NEU, mit min & max) ---
            if (BalanceTemperatureConfig.DATA.effectThresholds != null) {
                for (com.frogus.drinkordie.data.BalanceTemperatureData.EffectThreshold t : BalanceTemperatureConfig.DATA.effectThresholds) {
                    if (value >= t.min && value < t.max) {
                        if (t.slowness >= 0)
                            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, t.slowness, true, true));
                        if (t.weakness >= 0)
                            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, t.weakness, true, true));
                        if (t.nausea >= 0)
                            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, t.nausea, true, true));
                        if (t.damage > 0 && player.tickCount % 60 == 0) {
                            player.hurt(player.damageSources().starve(), t.damage);
                        }
                        break; // Nur einen Bereich pro Tick!
                    }
                }
            }
        });
    }
}
