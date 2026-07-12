package com.kangsuek.arrowescape

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.webkit.WebView

// Arrow Escape 안드로이드 껍데기.
// 게임 로직은 web/index.html(단일 진실 소스)을 그대로 쓴다 — 빌드 시 그 사본이
// app/src/main/assets/index.html 로 복사되고(scripts/sync-web.sh), WebView 가 로드한다.
// 웹을 수정하면 sync-web.sh 실행 후 다시 빌드하면 반영된다.
class MainActivity : Activity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        webView.settings.apply {
            javaScriptEnabled = true                     // 게임은 캔버스 + JS
            domStorageEnabled = true                     // localStorage (음소거 설정 저장)
            mediaPlaybackRequiresUserGesture = false     // WebAudio 효과음
        }
        webView.setBackgroundColor(0xFF1B1D28.toInt())   // 게임 배경색과 일치
        webView.loadUrl("file:///android_asset/index.html")
    }

    // 뒤로가기: 웹 히스토리가 있으면 뒤로, 없으면 앱 종료
    @Deprecated("deprecated in API 33; 단일 화면 게임이라 단순 처리로 충분")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}
