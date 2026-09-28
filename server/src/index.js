import express from "express";
import { GoogleGenAI } from "@google/genai";

const app = express();
app.disable("x-powered-by");
app.use(express.json({ limit: "64kb" }));
const port = Number(process.env.PORT || 8080);
const apiKey = process.env.GEMINI_API_KEY;
const proxyToken = process.env.MOCMAY_PROXY_TOKEN;
const model = process.env.GEMINI_MODEL || "gemini-3.8-flash";
const ai = apiKey ? new GoogleGenAI({ apiKey }) : null;
const buckets = new Map();
const WINDOW_MS = 60_000;
const MAX_REQUESTS = Number(process.env.RATE_LIMIT_PER_MINUTE || 30);
const MAX_INPUT = Number(process.env.MAX_INPUT_CHARS || 20_000);

function clientKey(req) { return req.ip || req.socket.remoteAddress || "unknown"; }
function allowed(req) {
  const now = Date.now(), key = clientKey(req);
  const old = buckets.get(key) || [];
  const fresh = old.filter(t => now - t < WINDOW_MS);
  if (fresh.length >= MAX_REQUESTS) return false;
  fresh.push(now); buckets.set(key, fresh); return true;
}
function authorized(req) {
  return !!proxyToken && req.get("X-MocMay-Token") === proxyToken;
}

app.get("/health", (_req, res) => res.json({ ok: true, service: "moc-may-gemini-proxy", model }));
app.post("/chat", async (req, res) => {
  try {
    if (!ai) return res.status(503).json({ error: "Gemini backend is not configured." });
    if (!authorized(req)) return res.status(401).json({ error: "Unauthorized." });
    if (!allowed(req)) return res.status(429).json({ error: "Rate limit exceeded." });
    const input = typeof req.body?.input === "string" ? req.body.input.trim() : "";
    if (!input) return res.status(400).json({ error: "input is required" });
    if (input.length > MAX_INPUT) return res.status(413).json({ error: "input too large" });

    const options = { model, input, tools: [{ type: "google_search" }] };
    const previous = typeof req.body?.previous_interaction_id === "string" ? req.body.previous_interaction_id.trim() : "";
    if (previous) options.previous_interaction_id = previous;
    const interaction = await ai.interactions.create(options);
    res.json({ output_text: interaction.output_text || "", interaction_id: interaction.id || "" });
  } catch (e) {
    console.error(e);
    res.status(502).json({ error: String(e?.message || e) });
  }
});

app.listen(port, "0.0.0.0", () => console.log(`Moc May Gemini proxy listening on ${port}`));
