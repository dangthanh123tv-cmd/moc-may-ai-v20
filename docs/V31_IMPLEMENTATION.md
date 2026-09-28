# V31 implementation

## Integrated
1. GGUF/llama.cpp offline inference from V29.
2. Thermal Guard and device diagnostics.
3. SQLite chat memory/session.
4. Local Knowledge Store in SQLite.
5. Safe calculator with a small arithmetic parser.
6. Security input limits and HTTPS endpoint validation.
7. Gemini proxy client with interaction continuity.
8. Unified routing and offline fallback.
9. GitHub Actions static checks and Android build for v31 tags.

## Security
- Google API key is server-only.
- Android accepts HTTPS endpoints only.
- Proxy token is separate from the Google API key.
- GGUF stays in app-private storage.
- No external plugin execution is enabled.

## Known production hardening
- Put the proxy token in Android Keystore for stronger at-rest protection.
- Put the Gemini API key in a managed secret store.
- Replace the in-memory rate limiter with a shared limiter for multi-instance deployments.
- Consider an allowlist for the production proxy hostname.
