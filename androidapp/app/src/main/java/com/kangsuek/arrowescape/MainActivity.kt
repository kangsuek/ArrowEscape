package com.kangsuek.arrowescape

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

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

        // edge-to-edge(타깃 SDK 35+)에서 WebView 가 상태바·내비바 뒤까지 그려져 헤더가
        // 가려진다. WebView 는 네이티브 padding 을 웹 레이아웃에 반영하지 않으므로,
        // 시스템 바 높이(px→CSS px)를 계산해 페이지 로드 후 CSS 변수로 주입한다.
        // 웹의 body 가 --safe-top/--safe-bottom 만큼 여백을 줘 시스템 바와 안 겹친다.
        val sbId = resources.getIdentifier("status_bar_height", "dimen", "android")
        val nbId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val density = resources.displayMetrics.density
        val topCss = (if (sbId > 0) resources.getDimensionPixelSize(sbId) else 0) / density
        val bottomCss = (if (nbId > 0) resources.getDimensionPixelSize(nbId) else 0) / density
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.evaluateJavascript(
                    "document.documentElement.style.setProperty('--safe-top','${topCss}px');" +
                    "document.documentElement.style.setProperty('--safe-bottom','${bottomCss}px');" +
                    "window.dispatchEvent(new Event('resize'));", null)
            }
        }

        webView.loadUrl("file:///android_asset/index.html")
    }

    // 뒤로가기는 기본 동작(앱 종료)에 맡긴다: 게임은 단일 페이지라 WebView 히스토리가
    // 쌓이지 않아 canGoBack() 이 항상 false — 별도 오버라이드가 필요 없다(API 33 에서
    // deprecated 된 onBackPressed() 도 피한다).
}
