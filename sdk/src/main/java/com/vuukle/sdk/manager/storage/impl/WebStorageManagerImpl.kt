package com.vuukle.sdk.manager.storage.impl

import android.util.Log
import android.webkit.WebView
import com.vuukle.sdk.constants.logger.LoggerConstants
import com.vuukle.sdk.manager.storage.WebStorageManager

class WebStorageManagerImpl : WebStorageManager {

    override fun putData(webView: WebView, key: String, value: String) {
        val injection = "javascript:window.localStorage.setItem('$key', '${value}')"
        webView.evaluateJavascript(injection) { result ->
            Log.e("testing---->>>", "saveLocalStorage $result")
            Log.e(LoggerConstants.VUUKLE_LOGGER, "saveLocalStorage $result")
        }
    }

    override fun getStringData(webView: WebView, key: String): String? {
        var mResult: String? = null
        webView.evaluateJavascript("javascript:window.localStorage.getItem('$key')") { result ->
            mResult = result
        }
        return mResult
    }
}