package com.frogus.drinkordie.temperature;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.minecraftforge.common.capabilities.CapabilityToken;


public class PlayerTemperatureProvider implements ICapabilityProvider {
    public static Capability<PlayerTemperature> TEMPERATURE_CAP;

    static {
        TEMPERATURE_CAP = CapabilityManager.get(new CapabilityToken<>() {});
    }

    private final PlayerTemperature instance = new PlayerTemperature();
    private final LazyOptional<PlayerTemperature> optional = LazyOptional.of(() -> instance);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == TEMPERATURE_CAP ? optional.cast() : LazyOptional.empty();
    }
}
