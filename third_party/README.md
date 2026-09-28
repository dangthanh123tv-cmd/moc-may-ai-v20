# llama.cpp source

V20 does not redistribute the llama.cpp source tree inside this ZIP. The build can fetch the pinned upstream release `v0.4.1` (commit `b29c606`) into `third_party/llama.cpp`.

This keeps the app repository smaller and makes the dependency explicit. For a fully offline Android build, run `scripts/fetch-llama.sh` once before Gradle/CMake.
