package com.frogus.drinkordie.hydration;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerHydration implements INBTSerializable<CompoundTag> {
    private float hydration = 100f;

    public float getHydration() { return hydration; }
    public void setHydration(float value) { hydration = Math.max(0f, Math.min(100f, value)); }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Hydration", hydration);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Hydration")) {
            this.hydration = nbt.getFloat("Hydration");
        }
    }
}
