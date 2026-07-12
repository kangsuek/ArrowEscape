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

전제: **Android Studio** 또는 Android SDK(platform android-36) + JDK 17+.
Gradle 래퍼가 포함돼 있어 별도 gradle 설치는 필요 없다.

```bash
androidapp/scripts/sync-web.sh   # 웹 최신본을 assets 로 복사 (웹 수정 후 매번)
```

그다음:
- **Android Studio**: `androidapp/` 를 열고 실행(▶) — 에뮬레이터/기기에 설치.
- **CLI**: `cd androidapp && ./gradlew assembleDebug`
  → `app/build/outputs/apk/debug/app-debug.apk`. 기기 설치는 `./gradlew installDebug`.
  (CLI 는 `JAVA_HOME` 을 JDK 17+ 로. Android Studio 번들 JDK 예:
  `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`)

APK 산출(릴리스): `./gradlew assembleRelease` → `app/build/outputs/apk/release/`.
(서명 미설정 — 배포하려면 서명 구성 추가)

버전: AGP 8.11.1 · Gradle 8.13 · Kotlin 2.1.10 · compileSdk/targetSdk 36 · minSdk 24.
(이 개발 머신에 설치된 SDK 플랫폼이 android-36 뿐이라 36 으로 맞췄다. `app-debug.apk`
빌드·패키징 검증 완료 — assets/index.html·아이콘 번들 확인.)

## 웹 수정 반영

핵심 기능은 항상 `web/index.html` 에 개발한다. 수정 후 `sync-web.sh` 실행 →
다시 빌드하면 앱에 반영된다.
