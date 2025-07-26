package com.frogus.drinkordie.hydration;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerHydrationProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<PlayerHydration> HYDRATION_CAP =
            CapabilityManager.get(new CapabilityToken<>() {});

    private final PlayerHydration instance = new PlayerHydration();
    private final LazyOptional<PlayerHydration> optional = LazyOptional.of(() -> instance);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == HYDRATION_CAP ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() { return instance.serializeNBT(); }

    @Override
    public void deserializeNBT(CompoundTag nbt) { instance.deserializeNBT(nbt); }
}
