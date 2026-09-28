# Local LLM v29

## Gợi ý cấu hình trên điện thoại

Bắt đầu bằng model GGUF nhỏ (khoảng 0.5B–1.5B) và context 2048. Model lớn hơn sẽ cần nhiều RAM hơn và có thể làm máy nóng nhanh.

Lệnh:

```text
/models
/model-info model.gguf
/load model.gguf 2048 4
/status
/device
```

Nếu máy nóng, hãy dùng `/unload`, để máy nguội rồi thử lại.
