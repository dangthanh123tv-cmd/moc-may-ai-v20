# Changelog

## v29.0.0

- Nâng version Android từ v20 lên v29.0.
- Thêm Thermal Guard dựa trên nhiệt độ pin: NORMAL / CAUTION / HOT.
- Giới hạn thread theo nhiệt độ để giảm tải.
- Chặn inference khi nhiệt độ pin ≥ 50°C.
- Thêm Device Info: Android/API/ABI/RAM/storage/nhiệt pin.
- Thêm cancel generation từ Java xuống JNI/native.
- Thêm nút Dừng.
- Thêm lịch sử chat và xóa lịch sử.
- SQLite schema v3 với index messages.
- Import GGUF bất đồng bộ để không khóa UI.
- Kiểm tra dung lượng trống trong lúc import.
- UI Android mới theo phong cách Mộc Mây: nâu/xanh lá pastel.
- CI cập nhật NDK và artifact naming.
