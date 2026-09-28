# Mộc Mây — Master Architecture V30

## Các dự án hiện có

1. `dangthanh123tv-cmd/moc-may-ai-v20` — Android native, branch V29; nguồn chính cho app trên SOG06.
2. `dangthanh123tv-cmd/moc-may-ai-v25-core` — lõi Python/Termux: router, memory/session, knowledge, diagnostics, web research, security, mobile profile.
3. `dangthanh123tv-cmd/moc-may-store` — website bán hàng React/Vite + Supabase + GitHub Pages.
4. `dangthanh123tv-cmd/moc-may-ai-web-demo` — web demo nhẹ để xem giao diện.

## Nguyên tắc hợp nhất

Không trộn 4 codebase thành một app duy nhất. Mỗi repo giữ đúng vai trò và giao tiếp qua API/contract chung.

```text
                    ┌─────────────────────────┐
                    │     Google Gemini       │
                    │ Interactions + Search   │
                    └────────────┬────────────┘
                                 │ HTTPS
                    ┌────────────▼────────────┐
                    │   Gemini Proxy/Cloud    │
                    │ key + auth + rate limit │
                    └───────┬─────────┬───────┘
                            │         │
                  ┌─────────▼─┐   ┌──▼──────────┐
                  │ Android   │   │ Web Store   │
                  │ V30       │   │ Supabase    │
                  └────┬──────┘   └─────────────┘
                       │
              ┌────────▼────────┐
              │ llama.cpp/GGUF  │
              │ OFFLINE FALLBACK│
              └─────────────────┘
                       ▲
                       │ shared logic/contracts
              ┌────────┴────────┐
              │ V25 Core/Termux  │
              │ memory/router/   │
              │ security/web     │
              └──────────────────┘
```

## Mục tiêu V30

- Offline GGUF/llama.cpp vẫn là fallback thực sự.
- Gemini Online dùng HTTPS, API key chỉ ở backend.
- Gemini Interactions API dùng `previous_interaction_id` cho hội thoại nhiều lượt.
- Google Search grounding chạy ở backend.
- Token proxy riêng + rate limit để giảm abuse.
- Thermal Guard 50°C tiếp tục chặn inference nóng.
- Không đưa API key vào APK, GitHub, web frontend hoặc log.
- GitHub Actions build/test trước khi phát hành.

## Không làm

- Không cho AI Studio ghi đè trực tiếp Android repo bằng web Vite/React.
- Không nhúng Gemini API key vào APK.
- Không đưa model GGUF lớn vào Git history.
- Không gộp Supabase service_role vào frontend.
