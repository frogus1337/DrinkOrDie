package com.frogus.drinkordie.sync;

import com.frogus.drinkordie.temperature.PlayerTemperature;
import com.frogus.drinkordie.temperature.PlayerTemperatureProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncTemperaturePacket {
    private final float temperature;

    public SyncTemperaturePacket(float temperature) {
        this.temperature = temperature;

    }

    public static void encode(SyncTemperaturePacket msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.temperature);
    }

    public static SyncTemperaturePacket decode(FriendlyByteBuf buf) {
        return new SyncTemperaturePacket(buf.readFloat());
    }

    public static void handle(SyncTemperaturePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = net.minecraft.client.Minecraft.getInstance().player;
            if (player != null) {
                LazyOptional<PlayerTemperature> tempCap = player.getCapability(PlayerTemperatureProvider.TEMPERATURE_CAP);
                tempCap.ifPresent(cap -> cap.setTemperature(msg.temperature));

            }
        });
        ctx.get().setPacketHandled(true);
    }
}
