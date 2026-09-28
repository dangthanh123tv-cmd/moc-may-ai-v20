package com.mocmay.ai;

/** V31 routing policy: deterministic local tools first, then local GGUF, then Gemini online, then local fallback. */
public final class UnifiedRouter {
    public enum Route { TOOL, LOCAL, ONLINE, FALLBACK }
    public Route route(String input, boolean onlineEnabled, boolean localReady) {
        String s = input == null ? "" : input.trim().toLowerCase(java.util.Locale.ROOT);
        if (s.startsWith("/calc ") || s.equals("/device") || s.equals("/status") || s.equals("/scan") || s.startsWith("/knowledge")) return Route.TOOL;
        if (onlineEnabled) return Route.ONLINE;
        return localReady ? Route.LOCAL : Route.FALLBACK;
    }
}
