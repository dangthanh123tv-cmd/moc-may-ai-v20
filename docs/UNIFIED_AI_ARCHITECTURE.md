# Mộc Mây AI V31 — Unified AI Architecture

Phạm vi: HỢP NHẤT CÁC DỰ ÁN AI, KHÔNG BAO GỒM website Mộc Mây Store.

## Nguồn hiện tại

- `dangthanh123tv-cmd/moc-may-ai-v20` — Android native, V29, llama.cpp/GGUF.
- `dangthanh123tv-cmd/moc-may-ai-v25-core` — core Python/Termux: router, memory/session, knowledge, diagnostics, mobile profile, security, web research.
- Google Gemini — online intelligence via a private HTTPS proxy.

## Mục tiêu V31

Android là ứng dụng chính. V25 Core là nguồn tham chiếu chức năng và có thể chạy độc lập trong Termux.
Không nhét Python vào APK một cách cưỡng ép. Các năng lực được chuyển sang Android theo từng module,
và các tác vụ nặng/online có thể đi qua HTTPS.

```text
                         Google Gemini
                Interactions + Search + Tools
                              |
                           HTTPS
                              |
                   +----------v-----------+
                   | Gemini Proxy         |
                   | Auth + Rate limit    |
                   | Secret only server   |
                   +----------+-----------+
                              |
             +----------------+----------------+
             |                                 |
       +-----v------+                    +-----v------+
       | Android    |                    | Termux Core|
       | V31        |                    | V25 Core   |
       | Java/C++   |                    | Python     |
       +-----+------+                    +------------+
             |
      +------+-----------------------------+
      |                                    |
 llama.cpp/GGUF                         Local SQLite
 offline inference                     memory/session
      |
 Thermal Guard / RAM / storage / security
```

## Router

1. Local deterministic tools: calculator, device/system info, text utilities.
2. Local memory/session.
3. Local GGUF inference.
4. Gemini online when explicitly enabled.
5. Gemini failure -> GGUF fallback.
6. Google Search grounding is backend-only.
7. Thermal Guard can block inference/network work at the configured cutoff.

## V25 Core capabilities to preserve

- Auto Router
- Memory + Session
- SQLite + migrations
- Knowledge Store
- Diagnostics
- Mobile Profile
- Thermal Guard 50°C
- GGUF/model validation
- HTTPS web research + SSRF hardening
- Security/sanitize
- Backup/update helpers
- Built-in calculator/system_info/text_tools

## Explicitly excluded

- `moc-may-store`
- `moc-may-ai-web-demo`
- Any Supabase store/admin/order code

## Source-of-truth rule

GitHub Android repository is the source of truth for the Android application.
AI Studio is an assisted coding/prototyping environment, not the canonical repository.
Never allow an AI Studio web template to replace the Android project.

## Security rules

- Never commit Gemini API keys.
- Never place a Gemini API key in APK/client code.
- Use a server-side secret.
- Require HTTPS.
- Authenticate the proxy.
- Rate-limit the proxy.
- Do not put GGUF model binaries in Git.
- Treat web/knowledge content as untrusted input.
