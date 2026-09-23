# Changelog

## v20.0.0
- Real llama.cpp C API integration path.
- GGUF model load/unload/inference JNI methods.
- CPU arm64 Android configuration for SOG06.
- Model import made transactional.
- Added model load command and status reporting.
- Added pinned llama.cpp v0.4.1 bootstrap script.
- Added GitHub Actions Android build workflow.
- Fixed malformed v20 LocalAgent help string.
- Kept native runtime optional so the source-only ZIP remains buildable without third-party source.
