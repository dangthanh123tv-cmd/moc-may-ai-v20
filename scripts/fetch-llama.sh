#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
mkdir -p "$ROOT/third_party"
if [ -d "$ROOT/third_party/llama.cpp/.git" ]; then
  git -C "$ROOT/third_party/llama.cpp" fetch --tags --depth 1 origin v0.4.1
  git -C "$ROOT/third_party/llama.cpp" checkout --detach v0.4.1
else
  git clone --depth 1 --branch v0.4.1 https://github.com/ggml-org/llama.cpp.git "$ROOT/third_party/llama.cpp"
fi
echo "llama.cpp: $(git -C "$ROOT/third_party/llama.cpp" rev-parse HEAD)"
