# Security v20

- No API keys or GitHub tokens are embedded.
- No root and no arbitrary shell execution.
- Models are copied to app-private storage only.
- Model names reject path separators, traversal, hidden names and oversized names.
- Import is transactional using `.part` and `.bak` files.
- Maximum model size: 6 GiB.
- GGUF magic header is validated before activation.
- SHA-256 is available for integrity checks.
- Native calls are serialized with a mutex.
- Online mode is informational/off by default.
- llama.cpp is an external dependency pinned to v0.4.1 in the build script.
