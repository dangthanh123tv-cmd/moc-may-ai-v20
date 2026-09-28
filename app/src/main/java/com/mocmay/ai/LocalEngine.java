package com.mocmay.ai;

import android.content.Context;

public final class LocalEngine {
    private final NativeLlmBridge nativeBridge = new NativeLlmBridge();
    private final ModelManager models;
    private final DeviceInfo device;
    private String loadedModel = null;
    public LocalEngine(Context c){models=new ModelManager(c);device=new DeviceInfo(c);}
    public boolean nativeAvailable(){return nativeBridge.isNativeAvailable();}
    public String backendInfo(){return nativeAvailable()?nativeBridge.nativeBackendInfo():"Native bridge unavailable";}
    public synchronized boolean load(String name,int context,int threads){
        try {
            if(!nativeAvailable()||!models.exists(name)) return false;
            if(device.thermal().state().equals("HOT")) return false;
            unload();
            int safeThreads=Math.min(Math.max(1,threads),device.thermal().recommendedThreads());
            boolean ok=nativeBridge.nativeLoadModel(models.file(name).getAbsolutePath(),context,safeThreads);
            if(ok) loadedModel=name; return ok;
        } catch(Exception e){return false;}
    }
    public synchronized void unload(){nativeBridge.nativeUnloadModel();loadedModel=null;}
    public synchronized void cancel(){nativeBridge.nativeCancelGeneration();}
    public synchronized String loadedModel(){return loadedModel;}
    public String generate(String prompt,int maxTokens){
        if(loadedModel==null) return "Mộc Mây AI đang ở chế độ fallback. Hãy import và tải một model GGUF để suy luận offline thật.";
        if(device.thermal().state().equals("HOT")) return "Thiết bị đang nóng (nhiệt pin ≥ 50°C). Mộc Mây AI đã chặn suy luận để bảo vệ máy. Hãy để máy nguội rồi thử lại.";
        int limit=Math.max(32,Math.min(512,maxTokens));
        String r=nativeBridge.nativeGenerate(prompt,limit);
        if(r.startsWith("__CANCELLED__")) return "Đã dừng sinh nội dung.";
        if(r.startsWith("__")) return "Native inference lỗi: "+r;
        return r.isEmpty()?"(Model không sinh ra nội dung.)":r;
    }
}
