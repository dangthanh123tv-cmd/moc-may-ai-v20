# Gemini V31

Google Gemini is integrated through a server-side proxy.

## Current online path

Android -> HTTPS proxy -> Gemini Interactions API -> response.

The proxy can use Google Search grounding.

## Model

Default production model in this kit:

`gemini-3.8-flash`

Keep the model configurable with `GEMINI_MODEL` so it can be changed without rebuilding the APK.

## Future multimodal modules

The architecture leaves room for:

- image understanding
- PDF/document understanding
- voice/Live API
- structured JSON output
- function calling
- embeddings/RAG
- image generation

These should be added as separate capabilities rather than making the Android chat path depend on every Gemini feature.

## Data/security

API keys stay on the server. Never paste a key into Git, the APK, or chat history.
