package com.vuukle.sdk.helpers

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView

object VuukleWebViewConfigurationHelper {

    @SuppressLint("SetJavaScriptEnabled")
    fun configure(webView: WebView) {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            setSupportZoom(false)

            // Use the system WebView's own user agent and append our SDK tag.
            // Previously we hardcoded "Chrome/90.0.4430.91" which third-party ad
            // exchanges flag as a fake / bot user agent.
            userAgentString = "$userAgentString VuukleSDK/${com.vuukle.sdk.BuildConfig.LIBRARY_VERSION}"

            // Required for AMP content (https) loading sub-resources over the
            // same protocol - default in API 26+ is COMPATIBILITY_MODE which is
            // what we want; set it explicitly for older API levels.
            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE

            // SECURITY: do NOT enable file/content access in production. The
            // previous values (true) allowed loaded JS to read local files.
            allowFileAccess = false
            allowContentAccess = false

            loadWithOverviewMode = true
            mediaPlaybackRequiresUserGesture = false
            setSupportMultipleWindows(true)
            javaScriptCanOpenWindowsAutomatically = true

            // pluginState was removed entirely in API 23 - calling it crashes
            // on Android 6.0+. Removed.
        }
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
    }
}
