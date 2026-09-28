#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail

ROOT="${1:-$HOME/moc-may-ai-v20}"
CORE="${2:-$HOME/moc-may-ai-v25-core}"

echo "== Mộc Mây AI V31 unified preparation =="

if [ ! -d "$ROOT/.git" ]; then
  echo "Android repo not found: $ROOT"
  echo "Clone it first:"
  echo "git clone https://github.com/dangthanh123tv-cmd/moc-may-ai-v20.git \"$ROOT\""
  exit 1
fi

if [ ! -d "$CORE/.git" ]; then
  echo "V25 Core not found: $CORE"
  echo "Clone it first:"
  echo "git clone https://github.com/dangthanh123tv-cmd/moc-may-ai-v25-core.git \"$CORE\""
  exit 1
fi

cd "$ROOT"
git fetch origin
git checkout moc-may-ai-v29
git pull --ff-only origin moc-may-ai-v29

if git show-ref --verify --quiet refs/heads/moc-may-ai-v31-unified; then
  git checkout moc-may-ai-v31-unified
else
  git checkout -b moc-may-ai-v31-unified
fi

echo
echo "Android branch prepared: moc-may-ai-v31-unified"
echo
echo "Next:"
echo "1) Copy the server/ and docs/ directories from this kit."
echo "2) Apply the Android Java patch."
echo "3) Run ./gradlew assembleDebug -PMOCMAY_WITH_LLAMA_CPP=true --no-daemon"
echo "4) Run static checks."
echo "5) Commit and push only after the build passes."
echo
echo "V25 Core remains a separate Termux source/reference project."
