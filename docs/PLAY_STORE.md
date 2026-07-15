# Google Play 등록 체크리스트

`androidapp/`(WebView 껍데기)을 Google Play 에 올리기 위한 준비 상태와 남은
수동 작업을 정리한 문서. 코드로 되는 부분은 이미 처리했고, 계정·서명 키·실제
콘솔 제출처럼 사람이 직접 해야 하는 부분만 아래 체크리스트로 남긴다.

## 이미 처리된 것

- **`INTERNET` 권한** — `AndroidManifest.xml`에 추가함. 온라인 순위표(Firebase
  REST fetch)가 인터넷을 쓰는데 예전 주석("로컬 assets 만 로드")은 틀린 상태였다.
- **릴리스 서명 스캐폴딩** — `app/build.gradle`이 `androidapp/keystore.properties`
  (git 제외)가 있으면 자동으로 release 빌드에 서명한다. 생성 절차는
  `androidapp/README.md`의 "Play 스토어용 릴리스 서명" 절 참조.
- **개인정보처리방침 초안** — `docs/privacy.html` (영어+한국어). GitHub Pages로
  게시하면 된다(아래 "개인정보처리방침 게시" 참조).
- **스토어 그래픽 자산** — `androidapp/store/`:
  - `play_icon_512.png` — 512×512 고해상도 아이콘 (기존 `macapp/build/icon.png`
    1024px 원본에서 리사이즈, 마스코트 디자인과 동일)
  - `feature_graphic_1024x500.png` — 1024×500 피처 그래픽 (소스: `feature_graphic.html`,
    수정 후 브라우저로 열어 1024×500 영역만 캡처하면 재생성 가능)
  - `screenshot_phone_1.png`, `_2.png`, `_3.png` — 세로 폰 스크린샷 3장(1080×2400,
    Medium_Phone AVD 에뮬레이터에 실제 설치·실행해 `adb shell screencap`으로 캡처한
    진짜 게임 화면, `androidapp/scripts/sync-web.sh`로 최신 `web/index.html`을 assets에
    동기화한 뒤 캡처 — 영어 UI). 1은 일반 모드 레벨 1(포털 지형), 2는 타임어택 HUD
    (카운트다운 타이머), 3은 타임오버 화면의 순위표 등록 폼(이름·국가 입력 UI가
    보이도록 — 실제 점수 제출은 하지 않음, Firebase 운영 DB에 테스트 데이터를
    남기지 않기 위해) — Play 최소 2장 요건 충족.
  - ⚠️ **주의**: 이전에 `androidapp/app/src/main/assets/index.html`이 `web/index.html`과
    동기화되지 않아(오래된 한국어 UI 빌드) 앱이 구버전으로 실행되고 있었다.
    Android 로 확인·캡처하기 전에는 항상 `sync-web.sh` 실행 후 재빌드할 것.

## 남은 작업 (사람이 직접)

### 1. Google Play 개발자 계정
[play.google.com/console](https://play.google.com/console) 에서 계정 생성
(1회 $25). 이미 있다면 생략.

### 2. 릴리스 키 생성 + AAB 빌드
`androidapp/README.md`의 "Play 스토어용 릴리스 서명" 절대로 진행:
`keytool`로 키 생성 → `keystore.properties` 작성 → `./gradlew bundleRelease`.
**키스토어 파일과 비밀번호를 반드시 안전한 곳(비밀번호 관리자 등)에 백업** —
분실하면 이 앱을 다시는 업데이트할 수 없다(Play App Signing을 쓰면 업로드 키
분실 시에도 Google 지원으로 복구 가능하니 최초 업로드 때 활성화 권장).

### 3. 개인정보처리방침 게시
저장소 Settings → Pages → Source를 "Deploy from a branch", Branch를
`main` / `/docs`로 설정하면 몇 분 안에 아래 URL이 열린다(제가 API로 대신
바꾸지 않았습니다 — 저장소 설정 변경이라 직접 확인 후 켜시는 게 맞습니다):

```
https://kangsuek.github.io/ArrowEscape/privacy.html
```

이 URL을 Play Console → 앱 콘텐츠 → 개인정보처리방침에 등록.

### 4. Data Safety(데이터 보안) 설문 — 답변 초안
Play Console → 앱 콘텐츠 → 데이터 보안. 실제 문항 문구는 계속 바뀌므로
아래는 어떤 데이터를 어떻게 답해야 하는지의 **내용** 기준 가이드:

| 데이터 유형 | 수집 여부 | 비고 |
|---|---|---|
| 이름(닉네임) | 수집함 | 순위표 등록 시에만, 사용자가 직접 입력(최대 12자), 선택 사항, 다른 사용자에게 공개됨. 목적: 앱 기능(순위표 표시). 제3자 공유 안 함. 전송 시 암호화(HTTPS). 삭제 요청은 이메일로만 가능(앱 내 자동 삭제 없음). |
| 위치 | **수집 안 함** | 국가는 드롭다운에서 사용자가 직접 고르는 값이라 GPS·IP 기반 위치 데이터가 아님 — "위치" 항목이 아니라 사용자가 자발적으로 준 부가 정보에 가깝다(문항 문구에 따라 "개인 정보 > 기타"로 신고할 수도 있음 — 제출 전 최신 문항 정의를 한 번 확인 권장). |
| 앱 활동(레벨·점수) | 수집함 | 순위표 등록 시에만 함께 전송. 계정과 연결되지 않음. |
| 광고 ID·기기 식별자 | 수집 안 함 | 광고·분석 SDK 없음. |
| 정확한 위치·대략적 위치 | 수집 안 함 | — |

공통 답변: 데이터는 전송 중 암호화됨(HTTPS) / 사용자가 삭제 요청 가능(이메일,
앱 내 자동 삭제 기능은 없음) / 수집은 선택 사항(순위표를 안 쓰면 전혀 전송 안 됨).

### 5. 콘텐츠 등급(IARC) 설문
폭력·도박·성적 콘텐츠 전부 "없음"으로 답하면 됨. 다만 **순위표 닉네임이
다른 사용자에게 공개로 보이는 자유 텍스트**라 "사용자 간 상호작용/UGC" 관련
문항에는 정직하게 "예"로 답해야 한다. ⚠️ 아래 "제출 전 검토 필요" 참고.

### 6. 스토어 등록정보(제목·설명) — 복사해서 쓸 문구 초안

**제목** (30자 제한): `Arrow Escape`

**짧은 설명** (80자 제한):
```
Untangle snake arrows and clear every puzzle grid.
```

**긴 설명** (4000자 제한, 영어 기본):
```
Arrow Escape is a fast, satisfying puzzle game: the grid is tangled with
snake-shaped arrows, and tapping one sends it shooting straight out in the
direction it's pointing — unless something blocks the way, in which case it
bounces back. Clear every arrow to beat the level.

• 7 puzzle shapes that cycle as you climb — square, heart, tree, circle,
  diamond, bear, house — each with its own color palette.
• Portals and pipes appear on later levels, adding teleport jumps and
  winding tunnels to the escape routes.
• Every puzzle is generated to always be solvable.
• Time Attack mode: race the clock, chain escapes for bonus seconds, and
  climb the online leaderboard.
• Score keeps building across levels — no resets between puzzles.
• Clean, ad-free, offline-playable (only the optional online leaderboard
  needs a connection).
```

**한국어 버전 (추가 언어로 등록 시)**:
```
Arrow Escape는 격자에 뒤엉킨 뱀 모양 화살표를 클릭해 하나씩 탈출시키는
퍼즐 게임입니다. 화살표를 누르면 머리가 향한 방향으로 직진해 탈출하고,
막히면 튕겨 돌아옵니다. 모든 화살표를 내보내면 레벨 클리어!

• 사각형·하트·나무·동그라미·다이아·곰돌이·집, 7가지 판 모양이 순환
• 후반 레벨에는 포털과 파이프 지형이 등장해 탈출 경로가 더 복잡해짐
• 모든 퍼즐은 항상 풀 수 있도록 생성됨
• 타임어택 모드: 시간 안에 화살표를 탈출시켜 보너스 시간을 쌓고
  온라인 순위표에 도전
• 점수는 레벨이 올라가도 초기화되지 않고 계속 누적
• 광고 없음, 오프라인 플레이 가능(온라인 순위표만 인터넷 필요)
```

**카테고리**: 게임 > 퍼즐 · **콘텐츠 등급**: 전체 이용가(3+/Everyone 예상)

### 7. 스크린샷 보강
`androidapp/store/screenshot_phone_1.png` 하나뿐이라 최소 2장 요건을 못
채운다. 실기기나 Android Studio 에뮬레이터에서 2~4장 더 찍을 것 — 추천 장면:
타임어택 HUD(시간 표시 + 펄스), 클리어 화면(별점), 순위표 화면.

### 8. 업로드 및 심사
`app-release.aab` 를 Play Console → 프로덕션(또는 먼저 내부 테스트 트랙)에
업로드 → 위 데이터 보안·콘텐츠 등급·스토어 등록정보 입력 → 검토 제출.

## ⚠️ 제출 전 검토가 필요한 항목: 순위표 UGC 정책

Google Play의 사용자 생성 콘텐츠(UGC) 정책은 사용자가 서로 볼 수 있는 자유
텍스트(지금의 순위표 닉네임)가 있는 앱에 보통 아래를 요구한다:

- 부적절한 콘텐츠 **신고** 기능
- 신고된 콘텐츠를 **합리적 시간 안에 제거**하는 절차
- 콘텐츠 기준(약관) 고지

현재 구현은 클라이언트 측 금칙어 필터(`NAME_BLOCKLIST`, 우회 쉬움)만 있고,
**앱 안에 신고 기능이 없다.** 삭제도 개발자 이메일 문의로만 가능하고, 심지어
Firebase 보안 규칙(`.write: "!data.exists()"`)이 REST API로는 기존 항목 수정·
삭제를 막고 있어 Firebase 콘솔에 직접 들어가야 지울 수 있다.

작은 개인 프로젝트 규모에서는 낮은 리스크로 그냥 심사에 통과하는 경우도
많지만, Google 이 UGC 관련 질문에서 신고 기능 부재를 이유로 반려할 가능성은
있다. 제출 전에 아래 중 하나를 선택할 것:

1. **이대로 제출** — 반려되면 그때 대응(가장 빠름, 반려 위험 있음).
2. **앱 안에 최소한의 "신고" 버튼 추가** — 순위표 각 항목 옆에 신고 버튼을
   달아 개발자 이메일로 신고를 보내는 정도로도 정책 요구를 충족할 수 있다.
   (원하시면 이 부분은 별도로 구현해 드릴 수 있습니다 — 지금은 범위 밖이라
   손대지 않았습니다.)
3. **닉네임 필드를 없애고 사전 정의된 표시 이름만 허용** — UGC 자체를 없애
   정책 문항을 우회(순위표의 재미는 줄어듦).
