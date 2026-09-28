#!/data/data/com.termux/files/usr/bin/bash
set -euo pipefail

REPO_DIR="${1:-$HOME/moc-may-ai-v20}"
PATCH_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$REPO_DIR"

git fetch origin

git checkout moc-may-ai-v29
git pull --ff-only origin moc-may-ai-v29
if git show-ref --verify --quiet refs/heads/moc-may-ai-v30-gemini; then
  git checkout moc-may-ai-v30-gemini
else
  git checkout -b moc-may-ai-v30-gemini
fi

mkdir -p app/src/main/java/com/mocmay/ai server/src docs
cp "$PATCH_DIR/app/src/main/java/com/mocmay/ai/GeminiProxyClient.java" app/src/main/java/com/mocmay/ai/
cp "$PATCH_DIR/app/src/main/java/com/mocmay/ai/LocalAgent.java" app/src/main/java/com/mocmay/ai/
cp "$PATCH_DIR/server/package.json" server/
cp "$PATCH_DIR/server/Dockerfile" server/
cp "$PATCH_DIR/server/.dockerignore" server/
cp "$PATCH_DIR/server/.env.example" server/
cp "$PATCH_DIR/server/src/index.js" server/src/
cp "$PATCH_DIR/docs/"*.md docs/

python - <<'PY'
from pathlib import Path
p=Path('app/build.gradle')
s=p.read_text()
s=s.replace("versionCode 290", "versionCode 300")
s=s.replace("versionName '29.0.0'", "versionName '30.0.0'")
p.write_text(s)

p=Path('app/src/main/java/com/mocmay/ai/MainActivity.java')
s=p.read_text()
s=s.replace('Mộc Mây AI v29.0', 'Mộc Mây AI v30.0')
s=s.replace('Tắt mặc định. Không nhúng API key vào APK.', 'Dùng /online-url, /online, /offline. API key không nằm trong APK.')
p.write_text(s)

p=Path('.github/workflows/static-check.yml')
s=p.read_text()
s=s.replace('moc-may-ai-v29', 'moc-may-ai-v30-gemini')
s=s.replace("versionName '29.0.0'", "versionName '30.0.0'")
p.write_text(s)
PY

git add app server docs .github/workflows/static-check.yml
git commit -m "Upgrade Mộc Mây AI v30 with Gemini online"
git push -u origin moc-may-ai-v30-gemini

echo "DONE: branch moc-may-ai-v30-gemini pushed."
