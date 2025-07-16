package com.frogus.drinkordie.data;

import java.util.List;

public class BalanceTemperatureData {
    public float normal = 36.5f;
    public float min = 28.0f;
    public float max = 44.0f;
    public float adjustFactor = 0.008f;
    public float sprintBoost = 0.3f;
    public float walkBoost = 0.1f;
    public float fireBoost = 4.0f;
    public float biomeTemperatureMultiplier = 1.1f;
    public float lavaBoost = 3.0f;
    public float altitudeColdFactor = 0.009f;
    public float caveWarmFactor = 0.01f;
    public float daytimeBonus = 0.2f;
    public float nighttimePenalty = 0.2f;
    public float directSunlightBonus = 0.5f;


    public List<EffectThreshold> effectThresholds;

    public static class EffectThreshold {
        public float min;
        public String effect;
        public float damage;
    }
}
