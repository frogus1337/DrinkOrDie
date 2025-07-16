package com.frogus.drinkordie.data;

public class HydrationData {
    public final float hydration;
    public final float temperature;
    public float standingLossPerTick;
    public float walkingLossPerTick;
    public float sprintingLossPerTick;
    public float jumpLoss;
    public java.util.List<EffectThreshold> effectThresholds;

    public static class EffectThreshold {
        public float min, max;
        public int slowness, weakness, miningFatigue;
        public float damage;
        public int damageCooldown;
    }

    public HydrationData(float hydration, float temperature) {
        this.hydration = hydration;
        this.temperature = temperature;
    }
}
