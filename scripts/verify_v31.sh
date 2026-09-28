#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
test -f "$ROOT/app/build.gradle"
test -f "$ROOT/app/src/main/cpp/mocmay_llm_jni.cpp"
test -f "$ROOT/app/src/main/java/com/mocmay/ai/GeminiProxyClient.java"
test -f "$ROOT/app/src/main/java/com/mocmay/ai/UnifiedRouter.java"
test -f "$ROOT/app/src/main/java/com/mocmay/ai/SafeCalculator.java"
test -f "$ROOT/app/src/main/java/com/mocmay/ai/KnowledgeStore.java"
test -f "$ROOT/app/src/main/java/com/mocmay/ai/SecurityPolicy.java"
grep -q "versionName '31.0.0'" "$ROOT/app/build.gradle"
grep -q 'MOCMAY_HAS_LLAMA' "$ROOT/app/src/main/cpp/mocmay_llm_jni.cpp"
grep -q 'nativeCancelGeneration' "$ROOT/app/src/main/java/com/mocmay/ai/NativeLlmBridge.java"
if grep -RInE '(sk-[A-Za-z0-9_-]{20,}|AIza[0-9A-Za-z_-]{20,}|ghp_[A-Za-z0-9]{20,})' --exclude-dir=.git "$ROOT"; then
  echo 'Potential hard-coded secret detected.' >&2; exit 1
fi
echo 'Moc May AI V31 verification: OK'
