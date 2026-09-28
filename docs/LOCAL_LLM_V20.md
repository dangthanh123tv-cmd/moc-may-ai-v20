# Local LLM v20

V20 integrates the public llama.cpp C API behind `NativeLlmBridge`.

Upstream Android builds target `arm64-v8a`; the current llama.cpp documentation recommends `GGML_NATIVE=OFF`, `GGML_OPENMP=OFF`, `GGML_LLAMAFILE=OFF`, and Android NDK CMake configuration for portable arm64 builds.

The app deliberately keeps model files out of the APK. A GGUF model must be imported into app-private storage first. Runtime memory use depends heavily on quantization, context size and model architecture.
