#!/usr/bin/env bash
# Compila o Plexo (debug + release assinado) e corre os testes.
# Uso: ./tools/build-apk.sh [--release]
set -euo pipefail

cd "$(dirname "$0")/.."
MODE="${1:-debug}"

echo "▶ Plexo — build ($MODE)"
echo "  JDK: $(java -version 2>&1 | head -1)"

if [ "$MODE" = "--release" ]; then
  : "${RELEASE_KEYSTORE_PATH:?defina RELEASE_KEYSTORE_PATH}"
  : "${RELEASE_KEYSTORE_PASSWORD:?defina RELEASE_KEYSTORE_PASSWORD}"
  : "${RELEASE_KEY_ALIAS:?defina RELEASE_KEY_ALIAS}"
  : "${RELEASE_KEY_PASSWORD:?defina RELEASE_KEY_PASSWORD}"
  ./gradlew :app:testDebugUnitTest :app:assembleRelease --no-daemon --stacktrace
  OUT="app/build/outputs/apk/release/app-release.apk"
else
  ./gradlew :app:testDebugUnitTest :app:assembleDebug --no-daemon --stacktrace
  OUT="app/build/outputs/apk/debug/app-debug.apk"
fi

echo
echo "✔ APK: $OUT ($(du -h "$OUT" | cut -f1))"
echo "  Capturas: app/build/screenshots/"
