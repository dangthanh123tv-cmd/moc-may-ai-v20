package com.mocmay.ai;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import java.io.File;
import java.util.Locale;

public final class DeviceInfo {
    private final Context context;
    private final ThermalGuard thermal;
    public DeviceInfo(Context c) { context=c.getApplicationContext(); thermal=new ThermalGuard(context); }

    public long availableRamMb() {
        ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();
        ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryInfo(mi);
        return mi.availMem/(1024*1024);
    }
    public long freeStorageMb() { return context.getFilesDir().getUsableSpace()/(1024*1024); }
    public String summary() {
        double t=thermal.temperatureC();
        String temp=Double.isNaN(t)?"unknown":String.format(Locale.ROOT,"%.1f°C",t);
        return "Android "+Build.VERSION.RELEASE+" (API "+Build.VERSION.SDK_INT+")\n"+
                "ABI: "+Build.SUPPORTED_ABIS[0]+"\n"+
                "RAM trống: "+availableRamMb()+" MB\n"+
                "Storage trống: "+freeStorageMb()+" MB\n"+
                "Nhiệt pin: "+temp+" • "+thermal.state();
    }
    public ThermalGuard thermal(){return thermal;}
}
