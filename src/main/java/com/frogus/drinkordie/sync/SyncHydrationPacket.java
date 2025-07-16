package com.frogus.drinkordie.sync;

import com.frogus.drinkordie.hydration.PlayerHydrationProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class SyncHydrationPacket {
    private final float hydration;

    public SyncHydrationPacket(float hydration) {
        this.hydration = hydration;
    }

    public static void encode(SyncHydrationPacket msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.hydration);
    }

    public static SyncHydrationPacket decode(FriendlyByteBuf buf) {
        return new SyncHydrationPacket(buf.readFloat());
    }

    public static void handle(SyncHydrationPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getCapability(PlayerHydrationProvider.HYDRATION_CAP)
                        .ifPresent(cap -> cap.setHydration(msg.hydration));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
