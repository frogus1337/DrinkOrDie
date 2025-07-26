package com.frogus.drinkordie.data;

import java.util.Map;

public class HeatSourceConfig {
    public String block;
    public int range;
    public float temperatureBoost;
    public Map<String, Object> property; // z.B. "lit": true
}
