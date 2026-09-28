# Security notes

- Không nhúng API key vào APK.
- Model chỉ được nhận với tên file an toàn và phần mở rộng `.gguf`.
- Import dùng file tạm rồi rename sang file đích để tránh file dở dang.
- Kiểm tra GGUF magic header trước khi chấp nhận model.
- Model nằm trong app-private storage, không mở trực tiếp từ đường dẫn tùy ý.
- SHA-256 có thể dùng để kiểm tra tính toàn vẹn.
- Online connector bị khóa mặc định.
- Nhiệt độ dùng trong Thermal Guard là nhiệt độ pin Android, không phải CPU temperature.
