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

## 게임 기능

- **레벨 진행**: 12×12에서 시작해 레벨마다 커지며 최대 28×28. 판 모양은
  사각형 → 하트 → 나무 → 동그라미 → 다이아 → 곰돌이 → 집 순으로 순환(타임어택 모드도 동일하게 커진다).
- **점수·콤보·별점**: 연속 탈출 시 콤보 배율(최대 ×5), 낭비 클릭이 적을수록 별 3개.
  점수는 레벨이 올라가도 초기화되지 않고 누적된다(다음 판 시작 점수 = 이전 판 종료 점수).
- **타임어택 모드**(스톱워치 아이콘으로 켜기/끄기): 화살표가 탈출할 때마다 +1초,
  레벨 클리어 시 보너스 시간이 추가로 붙어 레벨을 이어 달린다. 오클릭은 시간을 깎는다.
  남은 시간이 10초 이하가 되면 초 단위로 숫자가 확대되는 펄스 연출과 120bpm 고정
  템포의 긴장감 BGM이 재생되고, 타임오버 시 엔딩 징글이 나온다.
- **기록 저장**: 일반 모드 최고 점수(달성 레벨과 함께), 타임어택 최고 도달 레벨
  (그때의 점수와 함께)을 저장(localStorage).
- **이어하기**: 일반 모드는 현재 레벨·누적 점수를 저장해 다음 실행에서 이어서 시작.
- **햅틱**: 탈출·충돌·클리어·타임오버에 진동(안드로이드 WebView, 그 외엔 무시).
- **사운드**: 파일 없이 WebAudio 로 합성(음소거 토글). 설정은 모두 브라우저에 저장된다.
- **UI 언어**: 게임 내 표시 텍스트(라벨·버튼·오버레이 문구)는 영어, 문서·주석은 한국어.

이 기능 상태는 모두 `localStorage`에 저장된다(`ae_*` 키): `ae_level`·`ae_total`(이어하기,
`ae_total`이 곧 누적 점수), `ae_best_score`·`ae_best_level`(일반 모드 기록),
`ae_best_ta_level`·`ae_best_ta_score`(타임어택 기록), `ae_timeattack`·`ae_muted`(설정).

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
