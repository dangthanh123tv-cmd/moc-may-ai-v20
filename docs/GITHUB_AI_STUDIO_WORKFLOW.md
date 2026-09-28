# GitHub + Google AI Studio workflow

1. GitHub là nguồn sự thật cho mã nguồn.
2. Google AI Studio dùng để nghiên cứu/prompt/prototype và có thể import GitHub.
3. Khi AI Studio tạo web app mặc định, không đồng bộ ngược vào Android V30.
4. Android native phải giữ Kotlin/Java + C++ llama.cpp.
5. Gemini production đi qua `server/`, không gọi API trực tiếp từ APK.
6. Mọi thay đổi phải qua branch → build/test → review → merge.

Google AI Studio hiện hỗ trợ import GitHub và tạo cả web app lẫn Android native; web là mặc định, nên phải chọn đúng nền tảng khi thử nghiệm.
