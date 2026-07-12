# Arrow Escape

격자에 얽힌 뱀 모양 화살표를 클릭해 전부 탈출시키는 퍼즐 게임.

## 디렉토리 구조

```
web/        게임 본체 (단일 진실 소스) — index.html 하나로 완결
macapp/     Mac 앱 (Electron 껍데기) — web/index.html 을 그대로 로드
androidapp/ Android 앱 (WebView 껍데기) — web/index.html 을 그대로 로드
docs/       설계 문서 (화살표 생성 규칙 등)
```

핵심 기능은 항상 `web/`에 개발한다. Mac·Android 앱은 자체 게임 로직 없이 웹을 감싸기만 한다.

## 실행

**웹**: `web/index.html`을 브라우저에서 연다.

**Mac 앱**:

```bash
cd macapp
npm install     # 최초 1회
npm start       # 개발 실행 (web/index.html 을 직접 로드)
npm run dist    # .app 패키징 (dist/mac*/Arrow Escape.app)
```

**Mac 배포용 DMG**:

```bash
macapp/scripts/build-dmg.sh   # → macapp/dist/Arrow Escape-<버전>.dmg
```

서명되지 않은 앱이므로 다른 맥에서는 첫 실행 시 우클릭 → 열기.

**Android 앱** (Android Studio 또는 SDK+JDK 17 필요):

```bash
androidapp/scripts/sync-web.sh   # web/index.html → assets 복사 (웹 수정 후 매번)
# 이후 Android Studio 에서 androidapp/ 열고 실행, 또는 cd androidapp && ./gradlew installDebug
```

자세한 내용은 [androidapp/README.md](androidapp/README.md).

**앱 아이콘**: 디자인 원본은 `macapp/scripts/icon-design.html`(캔버스), 이를 1024px
PNG 로 추출한 것이 `macapp/build/icon.png`. 수정 후 `macapp/scripts/make-icons.sh`를
실행하면 `icon.icns`가 재생성되고 다음 빌드부터 앱·DMG 에 자동 적용된다.

## 문서

- [화살표 생성 규칙](docs/GENERATION_RULES.md) — 퍼즐 생성 알고리즘과 풀이 가능성 보장 원리
