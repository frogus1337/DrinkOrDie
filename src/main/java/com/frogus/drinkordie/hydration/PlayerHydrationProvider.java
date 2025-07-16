package com.frogus.drinkordie.hydration;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerHydrationProvider implements ICapabilityProvider {
    public static final Capability<PlayerHydration> HYDRATION_CAP =
            net.minecraftforge.common.capabilities.CapabilityManager.get(new CapabilityToken<PlayerHydration>() {});

    private final PlayerHydration hydration = new PlayerHydration();
    private final LazyOptional<PlayerHydration> optional = LazyOptional.of(() -> hydration);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == HYDRATION_CAP ? optional.cast() : LazyOptional.empty();
    }

    // Hilfsfunktionen, um Hydration zu speichern/laden
    public void saveNBTData(CompoundTag nbt) {
        hydration.saveNBTData(nbt);
    }

    public void loadNBTData(CompoundTag nbt) {
        hydration.loadNBTData(nbt);
    }
}
