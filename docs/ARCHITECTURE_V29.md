# Kiến trúc Mộc Mây AI v29

```text
UI Android
   │
   ├── MainActivity
   │      ├── LocalAgent
   │      ├── ChatStore (SQLite)
   │      └── DeviceInfo / ThermalGuard
   │
   └── ModelManager
          └── app-private/models/*.gguf

LocalEngine
   └── NativeLlmBridge (JNI)
          └── llama.cpp
                 └── CPU arm64
```

## Luồng suy luận

1. Người dùng import GGUF.
2. ModelManager sao chép sang app-private storage bằng file tạm.
3. Kiểm tra tên file, kích thước và GGUF magic.
4. LocalEngine kiểm tra native runtime + thermal state.
5. JNI load model với GPU layers = 0, phù hợp CPU Android.
6. Generation có thể nhận yêu cầu cancel.
7. Kết quả được lưu vào lịch sử SQLite.

## Luồng an toàn nhiệt

- `<45°C`: bình thường.
- `45–<50°C`: giảm thread.
- `≥50°C`: chặn generation/load mới.

Đây là ngưỡng bảo vệ ứng dụng, không phải cam kết rằng CPU đang ở đúng nhiệt độ đó.
