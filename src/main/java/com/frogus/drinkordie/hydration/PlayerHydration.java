package com.frogus.drinkordie.hydration;

import net.minecraft.nbt.CompoundTag;

public class PlayerHydration {
    private float hydration = 100.0f;

    // Getter
    public float getHydration() {
        return hydration;
    }

    // Setter
    public void setHydration(float hydration) {
        this.hydration = Math.max(0.0f, Math.min(100.0f, hydration));
    }

    // NBT speichern
    public void saveNBTData(CompoundTag nbt) {
        nbt.putFloat("hydration", hydration);
    }

    // NBT laden
    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("hydration")) {
            hydration = nbt.getFloat("hydration");
        }
    }
}
