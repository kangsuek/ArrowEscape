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

버전: AGP 9.2.1 · Gradle 9.4.1 · Kotlin 2.2.10 · compileSdk/targetSdk 36 · minSdk 24.
(이 개발 머신에 설치된 SDK 플랫폼이 android-36 뿐이라 36 으로 맞췄다. `app-debug.apk`
빌드·패키징 검증 완료 — assets/index.html·아이콘 번들 확인.)

## 웹 수정 반영

핵심 기능은 항상 `web/index.html` 에 개발한다. 수정 후 `sync-web.sh` 실행 →
다시 빌드하면 앱에 반영된다.

## Play 스토어용 릴리스 서명

`app/build.gradle`은 `androidapp/keystore.properties`(git 제외)가 있으면 자동으로
release 빌드에 서명한다. 파일이 없으면 release 빌드는 **서명되지 않은** 산출물이라
로컬 테스트만 가능하고 Play Console 업로드는 불가능하다.

**1) 키스토어 생성** (최초 1회, 한 번 만들면 이후 모든 업데이트에 계속 같은 키를
써야 하므로 비밀번호와 파일을 안전한 곳에 백업해 둘 것 — 분실 시 이 앱ID로 더 이상
업데이트를 올릴 수 없다):

```bash
cd androidapp
keytool -genkeypair -v -keystore release.keystore -alias arrowescape \
  -keyalg RSA -keysize 2048 -validity 10000
```

대화형으로 비밀번호(2번 — 키스토어용, 키용. 같게 써도 됨)와 이름·조직 등을 물어본다.
`release.keystore`는 `androidapp/` 바로 아래 생기며 `.gitignore`에 이미 포함돼 있다.

**2) `keystore.properties` 작성**: `keystore.properties.example`을 복사해
`keystore.properties`로 저장하고 위에서 정한 비밀번호·별칭을 채운다.

**3) AAB 빌드** (Play Console은 APK 대신 App Bundle을 요구):

```bash
cd androidapp && ./gradlew bundleRelease
# → app/build/outputs/bundle/release/app-release.aab
```

Play App Signing을 쓰면(Play Console 기본 권장 방식) 이 업로드 키가 유출돼도
Google 쪽 서명 키 재발급으로 복구할 수 있다 — 최초 업로드 시 Play Console에서
활성화할 것.
