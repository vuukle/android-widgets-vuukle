package com.vuukle.sdk.listeners

import android.webkit.WebView
import com.vuukle.sdk.exeptions.VuukleException
import com.vuukle.sdk.model.VuukleEvent

interface VuukleActionListener {

    fun onOpenPopupWindow(url: String, webView: WebView)
    fun onSSOSignIn()
    fun onLogout()
    fun onReloadAndSave()
    fun onReloadAndRestore()
    fun onRecycleWindow()
    fun onEvent(event: VuukleEvent,  webView: WebView)
    fun onSendError(exception: VuukleException)
}