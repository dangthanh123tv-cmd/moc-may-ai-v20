package com.mocmay.ai;

public final class SecurityPolicy {
    public static final int MAX_INPUT_CHARS = 20000;
    private SecurityPolicy() {}
    public static String sanitizeInput(String value) {
        if (value == null) return "";
        String s = value.trim();
        if (s.length() > MAX_INPUT_CHARS) s = s.substring(0, MAX_INPUT_CHARS);
        StringBuilder out = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\n' || c == '\r' || c == '\t' || c >= 32) out.append(c);
        }
        return out.toString();
    }
    public static boolean isSafeEndpoint(String url) {
        try {
            java.net.URI u = java.net.URI.create(url == null ? "" : url.trim());
            return "https".equalsIgnoreCase(u.getScheme()) && u.getHost() != null
                    && u.getUserInfo() == null && u.getFragment() == null;
        } catch (Exception e) { return false; }
    }
}
