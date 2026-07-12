#!/bin/bash
# 웹 게임(web/index.html, 단일 진실 소스)을 안드로이드 앱 assets 로 복사한다.
# 웹을 수정한 뒤 이 스크립트를 실행하고 다시 빌드하면 앱에 반영된다.
# (mac 앱의 extraResources 복사 단계에 해당)

set -euo pipefail
cd "$(dirname "$0")/.."

SRC="../web/index.html"
DST="app/src/main/assets/index.html"

cp "$SRC" "$DST"
echo "✓ 동기화: $SRC → $DST"
