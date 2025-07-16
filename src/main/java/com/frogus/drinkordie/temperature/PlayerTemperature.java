package com.frogus.drinkordie.temperature;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerTemperature implements INBTSerializable<CompoundTag> {
    private float temperature = 36.5f; // Standardwert

    public float getTemperature() {
        return temperature;
    }
    public void setTemperature(float value) {
        temperature = Math.max(25.0f, Math.min(45.0f, value)); // Extremgrenzen (optional)
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Temperature", temperature);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.temperature = nbt.getFloat("Temperature");
    }
}
