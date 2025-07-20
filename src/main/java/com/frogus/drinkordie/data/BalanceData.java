package com.frogus.drinkordie.data;

import java.util.List;

public class BalanceData {
    public float standingLossPerTick = 0.1f;
    public float walkingLossPerTick = 0.15f;
    public float sprintingLossPerTick = 0.2f;
    public float jumpLoss = 0.1f;
    public List<EffectThreshold> effectThresholds;
    public float temperatureHydrationMultiplier = 0.05f;

    public static class EffectThreshold {
        public float min;
        public float max;
        public int slowness;
        public int weakness;
        public int miningFatigue;
        public float damage;
        public int damageCooldown; // in Ticks
    }
}
