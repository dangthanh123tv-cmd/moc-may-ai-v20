# Mộc Mây AI v29.0 — Android Offline-first

Bản nâng cấp toàn diện từ v20 cho Android ARM64, tập trung vào **offline-first, GGUF/llama.cpp, an toàn model, thermal guard, bộ nhớ, lịch sử chat và CI**.

## Điểm chính

- GGUF chạy local qua JNI/llama.cpp, không cần API key.
- Import model vào app-private storage, kiểm tra GGUF magic, tên file an toàn và SHA-256.
- Thermal Guard dùng **nhiệt độ pin** làm tín hiệu bảo vệ; đây không phải cảm biến CPU.
- Tự giảm số thread khi nhiệt độ pin cao.
- Chặn suy luận khi nhiệt độ pin đạt 50°C.
- Nút Dừng gửi tín hiệu hủy generation xuống native runtime.
- Lưu memory và lịch sử chat bằng SQLite.
- Kiểm tra RAM/storage trước khi vận hành model.
- CI build Android debug trên GitHub Actions.
- Giữ arm64-v8a làm ABI chính để tối ưu cho thiết bị Android hiện đại.

## Model

Import file `.gguf` từ nút **AI / Model → Import model GGUF**.

Sau đó dùng:

```text
/models
/model-info <ten.gguf>
/load <ten.gguf> 2048 4
```

Không đặt API key vào APK. Model được giữ trong thư mục riêng của ứng dụng.

## Build CI

Workflow: `.github/workflows/android-release.yml`

- JDK 17
- Android SDK 35
- NDK 29
- llama.cpp được checkout theo tag cố định `v0.4.1`
- Build debug APK và upload artifact

## Lưu ý WebGPU

WebGPU của trình duyệt là một runtime khác với native Android. Bản Android này **không phụ thuộc WebGPU/Vulkan của Chrome**. Nếu Web demo không có WebGPU, hãy dùng native APK để chạy GGUF local.
