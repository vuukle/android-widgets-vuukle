package com.vuukle.sdk.manager.storage

import android.webkit.WebView

interface WebStorageManager {

    fun putData(webView: WebView, key: String, value: String)

    fun getStringData(webView: WebView, key: String): String?
}