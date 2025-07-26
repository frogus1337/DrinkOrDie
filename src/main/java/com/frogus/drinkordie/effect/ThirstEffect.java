package com.frogus.drinkordie.effect;

import com.frogus.drinkordie.hydration.PlayerHydrationProvider;
import com.frogus.drinkordie.network.DrinkOrDieNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class ThirstEffect extends MobEffect {
    public ThirstEffect() {
        super(MobEffectCategory.HARMFUL, 0x7f5a1f);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide && entity instanceof ServerPlayer player) {
            if (player.tickCount % 40 == 0) { // alle 2 Sekunden
                player.getCapability(PlayerHydrationProvider.HYDRATION_CAP).ifPresent(hydration -> {
                    hydration.setHydration(hydration.getHydration() - (amplifier + 1));
                    // → Direkt nach Änderung syncen!
                    DrinkOrDieNetwork.sendHydrationUpdate(player, hydration.getHydration());
                });
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
