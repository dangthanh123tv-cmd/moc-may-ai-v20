# Mộc Mây AI v20 build

## What is real in v20
- Java app, private GGUF model storage, transactional import, SHA-256, Share/Intent.
- Native JNI bridge with two build modes.
- When `MOCMAY_WITH_LLAMA_CPP=ON` and llama.cpp is present, the JNI bridge loads GGUF through llama.cpp and performs CPU inference.
- Default local source build remains dependency-free and reports that the runtime is not bundled.

## Full native build
1. Install Android Studio + SDK + NDK.
2. Run `scripts/fetch-llama.sh`.
3. Build with CMake/Gradle using `MOCMAY_WITH_LLAMA_CPP=ON`.
4. ABI is intentionally `arm64-v8a` for SOG06.

GitHub Actions can perform these steps automatically with the included workflow.

## Device usage
Import a `.gguf` model, then chat:
`/models`
`/load <model-name> 2048 4`
`/status`
`/unload`

Start with a small quantized instruct model. Larger models can exceed RAM and trigger Android process termination.
