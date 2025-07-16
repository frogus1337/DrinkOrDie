package com.frogus.drinkordie.event;

import com.frogus.drinkordie.core.DrinkOrDie;
import com.frogus.drinkordie.temperature.PlayerTemperature;
import com.frogus.drinkordie.temperature.PlayerTemperatureProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID)
public class TemperatureEvents {

    private static final float NORMAL_TEMP = 36.5f;
    private static final float MIN_TEMP = 28.0f;
    private static final float MAX_TEMP = 44.0f;

    // Prozentuale Anpassungsrate (z.B. 0.02f = 2% pro Tick)
    private static final float ADJUST_FACTOR = 0.008f;

    // --- CAPABILITY ANHÄNGEN ---
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(
                    net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(DrinkOrDie.MODID, "temperature"),
                    new PlayerTemperatureProvider()
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;
        //if (player.isCreative() || player.isSpectator()) return;

        LazyOptional<PlayerTemperature> tempCap = player.getCapability(PlayerTemperatureProvider.TEMPERATURE_CAP);
        tempCap.ifPresent(temp -> {
            float value = temp.getTemperature();

            // --- Zieltemperatur bestimmen ---
            float targetTemperature = NORMAL_TEMP;

            // Aktivitäten
            if (player.isSprinting()) targetTemperature += 0.3f;
            if (player.getDeltaMovement().horizontalDistanceSqr() > 0.002 && !player.isPassenger()) targetTemperature += 0.1f;
            if (player.isOnFire()) targetTemperature += 4.0f;

            // Umwelt-Einflüsse
            BlockPos pos = player.blockPosition();
            ResourceKey<Biome> biomeKey = player.level().getBiome(pos).unwrapKey().orElse(null);

            if (biomeKey != null) {
                // Kalte Biome:
                if (biomeKey == Biomes.SNOWY_PLAINS || biomeKey == Biomes.SNOWY_TAIGA || biomeKey == Biomes.ICE_SPIKES
                        || biomeKey == Biomes.FROZEN_RIVER || biomeKey == Biomes.FROZEN_OCEAN) {
                    targetTemperature -= 1.2f;
                }

                // Warme Biome:
                if (biomeKey == Biomes.DESERT || biomeKey == Biomes.SAVANNA || biomeKey == Biomes.BADLANDS || biomeKey == Biomes.NETHER_WASTES) {
                    targetTemperature += 1.2f;
                }
            }

            // In der Nähe von heißen Blöcken
            if (player.level().getBlockState(pos.below()).is(net.minecraft.world.level.block.Blocks.LAVA)) {
                targetTemperature += 3.0f;
            }

            // Höhe (Berg oder Höhle)
            if (player.getY() > 64) targetTemperature -= (player.getY() - 64) * 0.009f;
            if (player.getY() < 64) targetTemperature += (64 - player.getY()) * 0.01f;


            // --- Temperatur proportional angleichen ---
            float diff = targetTemperature - value;
            double extra = 1;
            if (diff >=2 ) extra = 1.2;
            if (diff >=3 ) extra = 1.9 ;
            if (diff >=4 ) extra = 2.2;
            value += diff * (ADJUST_FACTOR * extra );

            // Clamp
            value = Math.max(MIN_TEMP, Math.min(MAX_TEMP, value));

            temp.setTemperature(value);

            // Temperatur nach jedem Tick an den Client syncen:
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                com.frogus.drinkordie.network.DrinkOrDieNetwork.INSTANCE.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new com.frogus.drinkordie.sync.SyncTemperaturePacket(value)
                );
                // Debug-Log
                // System.out.println("SERVER sendet SyncTemperature: " + value);
            }

            // (Optional: hier könntest du noch Effekte auslösen)
        });
    }
}
