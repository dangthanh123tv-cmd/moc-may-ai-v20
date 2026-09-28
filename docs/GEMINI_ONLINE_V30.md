# Mộc Mây AI v30 — Gemini Online

Android giữ nguyên llama.cpp/GGUF offline. Gemini chạy qua HTTPS proxy backend.

## App
```
/online-url https://YOUR-SERVICE.run.app/chat
/online
```
Tắt bằng `/offline`, xóa phiên bằng `/online-reset`.

## Backend
Biến môi trường:
- `GEMINI_API_KEY` bắt buộc
- `GEMINI_MODEL` mặc định `gemini-3.8-flash`
- `PORT` mặc định `8080`

Chạy:
```
cd server
npm install
GEMINI_API_KEY="..." npm start
```

Không commit API key vào GitHub và không nhúng API key vào APK. Production nên thêm authentication và rate limiting.
