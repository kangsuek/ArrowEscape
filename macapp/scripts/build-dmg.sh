#!/bin/bash
# Arrow Escape — mac용 DMG 빌드 스크립트
# 사용법: macapp/scripts/build-dmg.sh  (어디서 실행해도 됨)
# 산출물: macapp/dist/Arrow Escape-<버전>.dmg

set -euo pipefail

# 스크립트 위치 기준으로 macapp 디렉토리로 이동
cd "$(dirname "$0")/.."

# node/npm이 없으면 Homebrew로 설치
if ! command -v npm >/dev/null 2>&1; then
  if ! command -v brew >/dev/null 2>&1; then
    echo "✗ npm이 없고 Homebrew도 없어 자동 설치할 수 없습니다." >&2
    echo "  https://brew.sh 안내에 따라 Homebrew를 먼저 설치해주세요." >&2
    exit 1
  fi
  echo "▶ node.js가 없어 Homebrew로 설치 중..."
  brew install node
fi

# 의존성이 없으면 설치
if [ ! -d node_modules ]; then
  echo "▶ 의존성 설치 중 (최초 1회)..."
  npm install
fi

echo "▶ DMG 빌드 중... (web/index.html 이 번들에 복사됩니다)"
npx electron-builder --mac dmg

DMG="$(ls -t dist/*.dmg 2>/dev/null | head -1)"
if [ -z "$DMG" ]; then
  echo "✗ DMG 생성 실패" >&2
  exit 1
fi

echo ""
echo "✓ 완료: $(cd "$(dirname "$DMG")" && pwd)/$(basename "$DMG")"
echo "  (서명되지 않은 앱이므로 다른 맥에서는 우클릭 → 열기로 첫 실행)"
