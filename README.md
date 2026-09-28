# Mộc Mây AI V31 Unified

Android-first, offline-first AI with GGUF/llama.cpp, memory/session, local knowledge, safe calculator, device/thermal guard, and optional Gemini Online through a private HTTPS proxy.

## Scope
- Included: Android AI core + V25 capability mapping + Gemini backend.
- Excluded: Mộc Mây web store and web demo.
- Google Gemini API key stays on the server; never place it in the APK or Git.

## Router
`local deterministic tools → local GGUF → Gemini Online → local fallback`

## Android commands
Use `/help`. Key commands: `/load`, `/models`, `/calc`, `/knowledge-add`, `/knowledge-search`, `/online-url`, `/online-token`, `/online`, `/offline`, `/status`.

## Build
```bash
./scripts/fetch-llama.sh
gradle assembleDebug -PMOCMAY_WITH_LLAMA_CPP=true --no-daemon
```

## Gemini proxy
```bash
cd server
cp .env.example .env
# set GEMINI_API_KEY and MOCMAY_PROXY_TOKEN only in server environment
npm install
npm start
```
