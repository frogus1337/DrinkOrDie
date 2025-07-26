package com.frogus.drinkordie.event;

import com.frogus.drinkordie.hydration.PlayerHydrationProvider;
import com.frogus.drinkordie.temperature.PlayerTemperatureProvider;
import com.frogus.drinkordie.core.DrinkOrDie;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(
                    ResourceLocation.fromNamespaceAndPath(DrinkOrDie.MODID, "hydration"),
                    new PlayerHydrationProvider()
            );
            event.addCapability(
                    ResourceLocation.fromNamespaceAndPath(DrinkOrDie.MODID, "temperature"),
                    new PlayerTemperatureProvider()
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // Hydration übertragen
        event.getOriginal().getCapability(PlayerHydrationProvider.HYDRATION_CAP).ifPresent(oldCap -> {
            event.getEntity().getCapability(PlayerHydrationProvider.HYDRATION_CAP).ifPresent(newCap -> {
                newCap.setHydration(oldCap.getHydration());
            });
        });
        // Temperatur übertragen
        event.getOriginal().getCapability(PlayerTemperatureProvider.TEMPERATURE_CAP).ifPresent(oldCap -> {
            event.getEntity().getCapability(PlayerTemperatureProvider.TEMPERATURE_CAP).ifPresent(newCap -> {
                newCap.setTemperature(oldCap.getTemperature());
            });
        });
    }
}
