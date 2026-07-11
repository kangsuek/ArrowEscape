# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트

Arrow Escape — HTML5 캔버스 퍼즐 게임. 격자에 얽힌 뱀 모양 화살표를 클릭하면
머리 방향 직선으로 탈출하고, 막히면 튕겨 돌아온다. 모든 화살표를 내보내면 클리어.
UI·주석·문서·사용자 대화 모두 한국어.

```
web/index.html   게임 본체 (단일 진실 소스) — 단일 파일, 빌드·의존성 없음
macapp/          Mac 앱 (Electron 껍데기) — 자체 게임 로직 없음, web/ 을 그대로 로드
docs/            설계 문서 (GENERATION_RULES.md)
```

**핵심 기능은 항상 `web/index.html`에만 개발한다.** Mac 앱은 얇은 껍데기로 유지 —
개발 모드(`npm start`)는 저장소의 웹 파일을 직접 로드하므로 웹 수정이 앱 재시작만으로
반영되고, 패키징 시에만 사본이 번들에 복사된다(`extraResources`).

## 실행과 검증

- 웹 실행: `web/index.html`을 브라우저에서 열면 끝.
- Mac 앱: `cd macapp && npm start` (개발), `npx electron-builder --mac` (패키징 →
  `macapp/dist/mac/Arrow Escape.app`). 앱 로드는 반드시 `pathToFileURL()` 경유 —
  경로 공백("Arrow Escape.app") 때문에 `loadFile()`은 패키징 후 빈 화면이 된다.
- 자동화 검증(Playwright MCP): `file://`이 차단되므로 `python3 -m http.server 8931`
  (백그라운드)로 서빙한 뒤 `http://localhost:8931/web/index.html` 접속.
- 테스트 프레임워크는 없다. 표준 검증은 브라우저에서 `window.__game` 훅으로
  **자동 풀이 루프**를 돌리는 것: 탈출 직선이 뚫린 화살표를 찾아 `clickCell()`을
  반복 → 반드시 전부 사라지고 클리어 화면이 떠야 한다(남으면 생성 버그).
  훅 API는 GENERATION_RULES.md의 "테스트 훅" 섹션 참조. 이 훅은 의도적으로
  유지하는 개발 인터페이스이므로 제거하지 말 것.
- 생성 시간 예산: 50×50 기준 2~3초 이내(현재 ~2초). 생성 로직 수정 후에는
  `setLevel(12)`(50×50)로 시간·채움 밀도를 재측정할 것.

## 핵심 불변식: 항상 풀 수 있는 퍼즐

생성의 모든 부분이 이 보장 위에 서 있다 — **역방향 구성**: 화살표는 "자기보다
먼저 놓인 화살표만 있는 상태에서 탈출 가능"할 때만 배치되고, 따라서 놓인 순서의
역순으로 클릭하면 반드시 전부 풀린다. 화살표 제거는 남은 화살표에게 항상 유리
(단조성)하므로 어떤 순서로 풀어도 막다른 상태가 없다.

생성 로직을 수정할 때 이 불변식을 지키는 장치들:
- 탈출 광선은 전부 `traceRay()` 하나가 규칙이다 — 포털 점프·파이프 통과·순환
  가드 포함(GENERATION_RULES.md §2.5). 직선 산술을 인라인으로 다시 쓰지 말 것.
- `checkExit()`의 자기충돌 규칙(`j >= d`): 자기 몸통이 탈출 경로 위에 있을 때
  제때 비켜주는지 판정(d 는 traced 호 단위). 나선형 화살표가 걸러지고, 광선이
  파이프 옆면(`'wall'`)으로 끝나 영영 못 나가는 배치도 함께 걸러진다.
- 연장(`fillGaps`)·파종(`reseed`)은 `onLaterRay()`(나중에 놓인 화살표의 traced
  탈출 경로 침범 금지 — `raySet` 캐시)와 `selfRayBlocked()` 재검사를 통과해야만
  허용.
- `isSolvable()` 탐욕 시뮬레이션이 최종 안전망.

## 아키텍처 (web/index.html 내부 구조)

한 파일이지만 명확한 층이 있다:

1. **레벨·모양·구역** — `levelCfg()`(크기 `min(50, 26+lv*2)`, 레벨 1~10 모양 순환,
   11+ 풍경화), `cellZone()`/`buildMask()`(정규화 좌표 수식 → mask/zone).
   화살표는 자기 zone 안에서만 생성·이동하고 zone 팔레트 색을 입는다 —
   하트·나무·곰돌이·풍경화 그림이 유지되는 원리.
2. **생성 파이프라인** — `newGame()`: `placeTerrain()`(포털·파이프 지형을 화살표보다
   먼저 배치) → 후보 8개 × (`generateSnakes()` + `fillGaps()`) → 밀도 창 안에서
   난이도(`obvious×3 + freeCount`) 최소 후보 선택 → `reseed()` → `fillGaps()`.
   규칙·점수식·금지 배치의 상세와 설계 근거는 **GENERATION_RULES.md가 단일 진실
   소스** — 생성 로직을 바꾸면 반드시 함께 갱신한다(사용자가 기대하는 관례).
3. **난이도 장치** — 배치 시점 금지(trivial): 영원히 못 막는 화살표(rayEmpty 0),
   가장자리 바깥 향 머리(exitDist ≤ 2), 바깥 절반 바깥 향 머리
   (`outwardHeadBanned`, 30칸 미만 작은 zone 면제), 직선 5칸+. 배치 순서는
   "바깥 고리(반경 62% 밖) 우선 + 안쪽 무작위" — 테두리 회전 클릭 공략을 막는
   핵심이므로 순서 로직을 바꿀 때는 난이도 지표(시작 시 자유 화살표 위치,
   테두리 막힘 비율)를 재측정할 것.
4. **런타임·렌더링** — `occ[r][c]`에 화살표 인덱스 저장. 탈출 시작 시 즉시 칸을
   비우고, 애니메이션 종료 시 `snakes.splice()` 후 **occ의 인덱스를 보정**한다
   (이 대응이 깨지면 클릭이 엉뚱한 화살표를 잡는다). 그리기는 셀 중심 폴리라인
   + 머리 방향 traced 광선 연장 위에서 호길이 파라미터 `t` 창을 이동시키는 방식 —
   기차처럼 미끄러지는 탈출 애니메이션의 원리(`buildPts`/`pointAt`/`drawSnake`).
   파이프 굽이는 폴리라인에 자연히 포함되고, 포털 점프 세그먼트는 `breaks`로
   표시해 몸통을 두 조각으로 그린다. 지형은 파이프 몸통(화살표 아래) →
   화살표 → 파이프 윤곽·포털 고리(화살표 위) 순서로 그린다.

공용 검사 함수(`traceRay`, `countBends`, `outwardHeadBanned`, `onLaterRay`,
`selfRayBlocked`)는 생성·연장·파종 세 경로가 공유한다. 규칙을 바꿀 때 인라인으로
복사하지 말고 이 함수들을 수정할 것 — 과거에 세 곳 중복으로 규칙이 어긋난 적이
있어 통합했다.
