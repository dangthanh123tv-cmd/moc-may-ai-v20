package com.mocmay.ai;

import android.content.Context;
import org.json.JSONObject;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/** Secure-by-default HTTPS client for the Mộc Mây Gemini proxy. */
public final class GeminiProxyClient {
    private static final String PREFS = "mocmay_gemini";
    private final Context context;
    public GeminiProxyClient(Context c) { context = c.getApplicationContext(); }
    private android.content.SharedPreferences prefs() { return context.getSharedPreferences(PREFS, 0); }
    public boolean enabled() { return prefs().getBoolean("enabled", false); }
    public String endpoint() { return prefs().getString("endpoint", ""); }
    public boolean tokenConfigured() { return !prefs().getString("token", "").isEmpty(); }
    public boolean setEndpoint(String url) {
        try {
            URI u = URI.create(url.trim());
            if (!"https".equalsIgnoreCase(u.getScheme()) || u.getHost() == null) return false;
        } catch (Exception e) { return false; }
        prefs().edit().putString("endpoint", url.trim()).apply();
        return true;
    }
    public void setToken(String token) { prefs().edit().putString("token", token == null ? "" : token.trim()).apply(); }
    public void setEnabled(boolean value) { prefs().edit().putBoolean("enabled", value).apply(); }
    public void clearConversation() { prefs().edit().remove("interaction_id").apply(); }

    public String ask(String prompt) throws IOException {
        String url = endpoint();
        if (url.isEmpty()) throw new IOException("Chưa cấu hình Gemini endpoint.");
        HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
        try {
            c.setRequestMethod("POST");
            c.setConnectTimeout(15000);
            c.setReadTimeout(45000);
            c.setDoOutput(true);
            c.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            String token = prefs().getString("token", "");
            if (!token.isEmpty()) c.setRequestProperty("X-MocMay-Token", token);

            JSONObject body = new JSONObject();
            body.put("input", prompt == null ? "" : prompt);
            String previous = prefs().getString("interaction_id", "");
            if (!previous.isEmpty()) body.put("previous_interaction_id", previous);
            byte[] payload = body.toString().getBytes(StandardCharsets.UTF_8);
            try (OutputStream out = c.getOutputStream()) { out.write(payload); }

            int code = c.getResponseCode();
            InputStream in = code >= 200 && code < 300 ? c.getInputStream() : c.getErrorStream();
            String raw = read(in);
            if (code < 200 || code >= 300) throw new IOException("Gemini proxy HTTP " + code + ": " + raw);
            JSONObject response = new JSONObject(raw);
            String id = response.optString("interaction_id", "");
            if (!id.isEmpty()) prefs().edit().putString("interaction_id", id).apply();
            String text = response.optString("output_text", "");
            if (text.isEmpty()) throw new IOException("Proxy không trả output_text.");
            return text;
        } catch (org.json.JSONException e) {
            throw new IOException("Phản hồi Gemini proxy không hợp lệ.", e);
        } finally { c.disconnect(); }
    }

    private static String read(InputStream in) throws IOException {
        if (in == null) return "";
        StringBuilder s = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line; while ((line = r.readLine()) != null) s.append(line);
        }
        return s.toString();
    }
}
