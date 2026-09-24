package com.mocmay.ai;
import android.content.Context;

public final class LocalEngine {
    private final NativeLlmBridge nativeBridge = new NativeLlmBridge();
    private final ModelManager models;
    private String loadedModel = null;
    private volatile boolean generating = false;
    private volatile long lastInferenceMs = 0;
    private volatile int lastTokens = 0;
    public LocalEngine(Context c){models=new ModelManager(c);}
    public boolean nativeAvailable(){return nativeBridge.isNativeAvailable();}
    public String backendInfo(){return nativeAvailable()?nativeBridge.nativeBackendInfo():"Native bridge unavailable";}
    public synchronized boolean load(String name,int context,int threads){
        try {
            if(!nativeAvailable()) return false;
            if(!models.exists(name)) return false;
            boolean ok=nativeBridge.nativeLoadModel(
                models.file(name).getAbsolutePath(), context, threads
            );
            if(ok) loadedModel=name;
            return ok;
        } catch(Exception e) {
            return false;
        }
    }
    public synchronized void unload(){nativeBridge.nativeUnloadModel();loadedModel=null;}
    public synchronized String loadedModel(){return loadedModel;}
    public synchronized String generate(String prompt,int maxTokens){
        if(loadedModel==null)
            return "Mộc Mây AI đang ở chế độ fallback. Hãy import và tải một model GGUF để suy luận offline thật.";

        generating = true;
        long start = System.currentTimeMillis();

        try {
            String r = nativeBridge.nativeGenerate(prompt,maxTokens);
            lastInferenceMs = System.currentTimeMillis() - start;
            lastTokens = maxTokens;

            if(r == null) return "Native inference lỗi: null";
            if(r.startsWith("__")) return "Native inference lỗi: "+r;
            return r.isEmpty() ? "(Model không sinh ra nội dung.)" : r;
        } finally {
            generating = false;
        }
    }

    public boolean isGenerating(){
        return generating;
    }

    public long lastInferenceMs(){
        return lastInferenceMs;
    }

    public int lastTokens(){
        return lastTokens;
    }

    public synchronized void stop(){
        // V22.1: native inference hiện chạy trong JNI.
        // Cờ này dùng cho UI; native cancellation sẽ được bổ sung ở bước tiếp theo.
        generating = false;
    }
}
