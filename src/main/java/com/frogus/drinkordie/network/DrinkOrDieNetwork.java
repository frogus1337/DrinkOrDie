package com.frogus.drinkordie.network;

import com.frogus.drinkordie.sync.SyncHydrationPacket;
import com.frogus.drinkordie.sync.SyncTemperaturePacket;
import com.frogus.drinkordie.core.DrinkOrDie;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class DrinkOrDieNetwork {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(DrinkOrDie.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int id = 0;

        // Hydration Sync Packet
        INSTANCE.registerMessage(
                id++,
                SyncHydrationPacket.class,
                SyncHydrationPacket::encode,
                SyncHydrationPacket::decode,
                SyncHydrationPacket::handle
        );

        // Temperature Sync Packet
        INSTANCE.registerMessage(
                id++,
                SyncTemperaturePacket.class,
                SyncTemperaturePacket::encode,
                SyncTemperaturePacket::decode,
                SyncTemperaturePacket::handle
        );
    }
}
