'use strict';

// Arrow Escape Mac 앱 껍데기.
// 게임 로직은 저장소의 web/index.html(단일 진실 소스)을 그대로 로드한다.
// - 개발: 저장소의 웹 파일을 직접 로드 → 웹을 수정하면 앱 재시작만으로 반영
// - 배포: electron-builder 가 Resources 에 복사한 사본을 로드

const { app, BrowserWindow } = require('electron');
const path = require('path');
const fs = require('fs');
const { pathToFileURL } = require('url');

function htmlPath() {
  return app.isPackaged
    ? path.join(process.resourcesPath, 'index.html')
    : path.join(__dirname, '..', '..', 'web', 'index.html');
}

function createWindow() {
  const win = new BrowserWindow({
    width: 1280,
    height: 860,
    minWidth: 900,
    minHeight: 700,
    title: 'Arrow Escape',
    backgroundColor: '#1b1d28',
    webPreferences: {
      contextIsolation: true,
      nodeIntegration: false,
    },
  });
  // 경로에 공백이 있어도 안전하도록 file:// URL 로 인코딩해서 로드
  win.loadURL(pathToFileURL(htmlPath()).toString());

  // 로드 실패 시 원인을 파일로 남긴다 (~/Library/Application Support/Arrow Escape/load-error.log)
  win.webContents.on('did-fail-load', (e, code, desc, url) => {
    try {
      fs.appendFileSync(
        path.join(app.getPath('userData'), 'load-error.log'),
        `${new Date().toISOString()} code=${code} desc=${desc} url=${url}\n`
      );
    } catch {}
  });

  if (!app.isPackaged) {
    // 개발용 검증 로그: 게임이 실제로 보드를 생성했는지 확인
    win.webContents.once('did-finish-load', async () => {
      const n = await win.webContents
        .executeJavaScript('window.__game ? window.__game.snakes().length : -1')
        .catch(() => -1);
      console.log(`[verify] loaded: ${htmlPath()} / snakes = ${n}`);
    });
  }
}

app.whenReady().then(() => {
  createWindow();
  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') app.quit();
});
