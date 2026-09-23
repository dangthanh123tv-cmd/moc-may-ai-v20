package com.mocmay.ai;

public final class NativeLlmBridge {
    private static boolean loaded;
    static { try { System.loadLibrary("mocmay_llm"); loaded = true; } catch (UnsatisfiedLinkError ignored) { loaded = false; } }
    public boolean isNativeAvailable() { return loaded; }
    public native boolean nativeModelReadable(String absolutePath);
    public native boolean nativeIsGguf(String absolutePath);
    public native String nativeBackendInfo();
    public native boolean nativeLoadModel(String absolutePath, int context, int threads);
    public native void nativeUnloadModel();
    public native String nativeGenerate(String prompt, int maxTokens);
}
