#!/bin/bash
# Arrow Escape — 앱 아이콘(.icns) 생성 스크립트
# 입력: macapp/build/icon.png (1024×1024, 알파 포함)
#   (원본 디자인: macapp/scripts/icon-design.html 의 캔버스를 PNG 로 추출한 것)
# 출력: macapp/build/icon.icns — electron-builder 가 자동으로 앱·DMG 에 사용

set -euo pipefail
cd "$(dirname "$0")/../build"

if [ ! -f icon.png ]; then
  echo "✗ build/icon.png 이 없습니다 (1024×1024 PNG 필요)" >&2
  exit 1
fi

rm -rf icon.iconset
mkdir icon.iconset

for size in 16 32 128 256 512; do
  sips -z "$size" "$size" icon.png --out "icon.iconset/icon_${size}x${size}.png" > /dev/null
  double=$((size * 2))
  sips -z "$double" "$double" icon.png --out "icon.iconset/icon_${size}x${size}@2x.png" > /dev/null
done

iconutil -c icns icon.iconset -o icon.icns
rm -rf icon.iconset

echo "✓ 완료: $(pwd)/icon.icns"
