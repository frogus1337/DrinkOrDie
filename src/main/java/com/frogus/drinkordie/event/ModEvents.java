package com.frogus.drinkordie.event;

import com.frogus.drinkordie.hydration.PlayerHydrationProvider;
import com.frogus.drinkordie.core.DrinkOrDie;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.resources.ResourceLocation;

// Diese Klasse registriert Events
@Mod.EventBusSubscriber(modid = DrinkOrDie.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModEvents {

    // Das ist das Event, das Spieler-Entities Capability hinzufügt
    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(
                    ResourceLocation.fromNamespaceAndPath(DrinkOrDie.MODID, "hydration"),
                    new PlayerHydrationProvider()
            );
        }
    }
}
