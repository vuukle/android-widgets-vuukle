package com.vuukle.sdk.clients

import android.os.Build
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.vuukle.sdk.constants.logger.LoggerConstants
import com.vuukle.sdk.exeptions.NetworkConnectionLostException
import com.vuukle.sdk.handlers.VuukleExternalAppHandler
import com.vuukle.sdk.listeners.VuukleActionListener
import com.vuukle.sdk.listeners.VuukleErrorListener
import com.vuukle.sdk.listeners.WebViewStateListener
import com.vuukle.sdk.utils.EventUtil


class VuukleWebViewClient(
    private val identifier: Int,
    private val actionListener: VuukleActionListener? = null,
    private val openPopupCallback: ((url: String, webView: WebView) -> Unit)? = null,
    private val webViewStateListener: WebViewStateListener? = null,
    private val errorListener: VuukleErrorListener? = null,
) : WebViewClient() {

    private val externalAppHandler = VuukleExternalAppHandler()

    // Modern signature (API 24+). Default Android calls this on supported devices.
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest?): Boolean {
        val url = request?.url?.toString() ?: return false
        return handleUrl(view, url)
    }

    // Legacy signature (pre-API 24). Kept for backwards compatibility.
    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return handleUrl(view, url)
    }

    private fun handleUrl(view: WebView, url: String): Boolean {
        if (externalAppHandler.handleExternalApp(url)) {
            Log.i(LoggerConstants.VUUKLE_LOGGER, "external app handled url")
            return true
        }
        val event = EventUtil.createEventByUrl(url)
        if (event != null && actionListener != null) {
            actionListener.onEvent(event, view)
            return true
        } else if (openPopupCallback != null) {
            openPopupCallback.invoke(url, view)
            return true
        }
        return false
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        webViewStateListener?.onPageFinishLoad(url)
    }

    // Surface network and HTTP failures to publishers instead of failing silently
    // with a blank webview - the most common 'Vuukle widget doesn't load' bug.
    override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
        super.onReceivedError(view, request, error)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            errorListener?.onError(
                NetworkConnectionLostException(
                    "WebView failed to load: ${error?.description} (code ${error?.errorCode}) for url=${request?.url}"
                )
            )
        }
    }

    override fun onReceivedHttpError(view: WebView?, request: WebResourceRequest?, errorResponse: WebResourceResponse?) {
        super.onReceivedHttpError(view, request, errorResponse)
        errorListener?.onError(
            NetworkConnectionLostException(
                "WebView HTTP error: ${errorResponse?.statusCode} ${errorResponse?.reasonPhrase} for url=${request?.url}"
            )
        )
    }
}
