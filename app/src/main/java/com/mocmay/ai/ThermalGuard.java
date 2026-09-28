package com.mocmay.ai;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/** Battery-temperature guard. It does not claim to measure CPU temperature. */
public final class ThermalGuard {
    public static final double CAUTION_C = 45.0;
    public static final double LIMIT_C = 50.0;
    private final Context context;

    public ThermalGuard(Context context) { this.context = context.getApplicationContext(); }

    public double temperatureC() {
        try {
            Intent i = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (i == null) return Double.NaN;
            int raw = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Integer.MIN_VALUE);
            return raw == Integer.MIN_VALUE ? Double.NaN : raw / 10.0;
        } catch (Exception e) { return Double.NaN; }
    }

    public String state() {
        double t = temperatureC();
        if (Double.isNaN(t)) return "UNKNOWN";
        if (t >= LIMIT_C) return "HOT";
        if (t >= CAUTION_C) return "CAUTION";
        return "NORMAL";
    }

    public int recommendedThreads() {
        int cpu = Math.max(2, Runtime.getRuntime().availableProcessors());
        double t = temperatureC();
        if (!Double.isNaN(t) && t >= LIMIT_C) return 1;
        if (!Double.isNaN(t) && t >= CAUTION_C) return Math.min(2, cpu);
        return Math.max(2, Math.min(4, cpu / 2));
    }
}
