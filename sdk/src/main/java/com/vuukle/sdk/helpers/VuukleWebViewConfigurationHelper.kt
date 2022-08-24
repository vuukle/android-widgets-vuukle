package com.vuukle.sdk.helpers

import android.annotation.SuppressLint
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView

object VuukleWebViewConfigurationHelper {

    @SuppressLint("SetJavaScriptEnabled")
    fun configure(webView: WebView) {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(false)
        webView.settings.userAgentString = System.getProperty("http.agent")
            ?: "Mozilla/5.0 (Linux; Android 11) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.91 Mobile Safari/537.36"
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.settings.pluginState = WebSettings.PluginState.ON;
        webView.settings.loadWithOverviewMode = true
        webView.settings.mediaPlaybackRequiresUserGesture = false;
        webView.settings.setSupportMultipleWindows(true)
    }
}