package com.mocmay.ai;
public interface ModelEngine {
    String name();
    boolean isAvailable();
    String generate(String prompt, String memory);
    default void generateStreaming(String prompt, String memory, TokenListener listener){
        String s=generate(prompt,memory); if(listener!=null) listener.onText(s); if(listener!=null) listener.onDone();
    }
    interface TokenListener { void onText(String text); void onDone(); void onError(Throwable t); }
}
