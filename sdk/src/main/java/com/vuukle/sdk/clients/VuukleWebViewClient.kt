package com.vuukle.sdk.clients

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import com.vuukle.sdk.constants.logger.LoggerConstants
import com.vuukle.sdk.handlers.VuukleExternalAppHandler
import com.vuukle.sdk.listeners.VuukleActionListener
import com.vuukle.sdk.listeners.WebViewStateListener
import com.vuukle.sdk.utils.EventUtil

class VuukleWebViewClient(
    private val identifier: Int,
    private val actionListener: VuukleActionListener? = null,
    private val openPopupCallback: ((url: String, webView: WebView) -> Unit)? = null,
    private val webViewStateListener: WebViewStateListener? = null,
) : WebViewClient() {

    private val externalAppHandler = VuukleExternalAppHandler()

    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

        val isExternalAppOpened = externalAppHandler.handleExternalApp(url)
        Log.i(LoggerConstants.VUUKLE_LOGGER,"isExternalAppOpened = $isExternalAppOpened")
        if (isExternalAppOpened) return true

        Log.i(LoggerConstants.VUUKLE_LOGGER,"url = $url")
        val event = EventUtil.createEventByUrl(url)
        if(event != null && actionListener != null){
            Log.i(LoggerConstants.VUUKLE_LOGGER,"event = $event")
            actionListener.onEvent(event, view)
            return true
        } else if(openPopupCallback != null) {
            Log.i(LoggerConstants.VUUKLE_LOGGER,"open dialog from invoke")
            openPopupCallback.invoke(url, view)
            return true
        }

        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        webViewStateListener?.onPageFinishLoad(url)
    }
}