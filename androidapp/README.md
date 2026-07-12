# Arrow Escape — Android 앱

`web/index.html`(게임 본체, 단일 진실 소스)을 그대로 로드하는 얇은 WebView 껍데기.
자체 게임 로직은 없다. mac 앱(`macapp/`)과 같은 구조.

## 구조

```
app/src/main/
  java/.../MainActivity.kt   WebView 하나로 index.html 을 로드
  assets/index.html          web/index.html 의 사본 (sync-web.sh 로 생성, git 제외)
  res/                       런처 아이콘·테마·문자열
scripts/sync-web.sh          web/index.html → assets 복사
```

## 개발·빌드

전제: **Android Studio**(또는 Android SDK + JDK 17). 이 저장소를 만든 맥에는
안드로이드 도구가 없어 프로젝트 소스만 제공한다 — 빌드는 SDK가 있는 환경에서 한다.

```bash
androidapp/scripts/sync-web.sh   # 웹 최신본을 assets 로 복사 (웹 수정 후 매번)
```

그다음:
- **Android Studio**: `androidapp/` 를 열고 실행(▶) — 에뮬레이터/기기에 설치.
- **CLI**(SDK·JDK 설정 시): `cd androidapp && ./gradlew installDebug`
  (Gradle 래퍼 jar 는 Android Studio 최초 동기화 또는 `gradle wrapper` 로 생성됨)

APK 산출: `./gradlew assembleRelease` → `app/build/outputs/apk/release/`.
(서명 미설정 — 배포하려면 서명 구성 추가)

## 웹 수정 반영

핵심 기능은 항상 `web/index.html` 에 개발한다. 수정 후 `sync-web.sh` 실행 →
다시 빌드하면 앱에 반영된다.
