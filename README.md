# Arrow Escape

격자에 얽힌 뱀 모양 화살표를 클릭해 전부 탈출시키는 퍼즐 게임.

## 디렉토리 구조

```
web/       게임 본체 (단일 진실 소스) — index.html 하나로 완결
macapp/    Mac 앱 (Electron 껍데기) — web/index.html 을 그대로 로드
docs/      설계 문서 (화살표 생성 규칙 등)
```

핵심 기능은 항상 `web/`에 개발한다. Mac 앱은 자체 게임 로직 없이 웹을 감싸기만 한다.

## 실행

**웹**: `web/index.html`을 브라우저에서 연다.

**Mac 앱**:

```bash
cd macapp
npm install     # 최초 1회
npm start       # 개발 실행 (web/index.html 을 직접 로드)
npm run dist    # .app 패키징 (dist/mac*/Arrow Escape.app)
```

## 문서

- [화살표 생성 규칙](docs/GENERATION_RULES.md) — 퍼즐 생성 알고리즘과 풀이 가능성 보장 원리
