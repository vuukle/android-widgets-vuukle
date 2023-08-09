package com.vuukle.sdk.clients

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Message
import android.webkit.ConsoleMessage
import android.webkit.JsResult
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import com.vuukle.sdk.handlers.VuukleConsoleLogHandler
import com.vuukle.sdk.helpers.VuukleWebViewConfigurationHelper
import com.vuukle.sdk.listeners.VuukleActionListener
import com.vuukle.sdk.utils.ResultLauncherUtil
import com.vuukle.sdk.utils.VuukleAndroidUtil


class VuukleWebChromeClient(
    private val identifier: Int,
    private val actionListener: VuukleActionListener?,
    private val openPopupCallback: ((String, WebView) -> Unit)? = null,
    private val closeDialogClosure: () -> Unit,
    private val isDialog:(Boolean) -> Unit
) : WebChromeClient() {

    var window: WebView? = null
    var resultMessage: Message? = null
    private val consoleMessageHandler = VuukleConsoleLogHandler(identifier)

    override fun onJsAlert(
        view: WebView?,
        url: String?,
        message: String?,
        result: JsResult?,
    ): Boolean {
        Toast.makeText(VuukleAndroidUtil.getActivity(), message, Toast.LENGTH_LONG).show()
        return super.onJsAlert(view, url, message, result)
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        consoleMessageHandler.handle(consoleMessage.message().toString(), consoleMessage.sourceId())
        return super.onConsoleMessage(consoleMessage)
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?,
    ): Boolean {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        ResultLauncherUtil.openFileChooser(intent, filePathCallback)

        return true;
    }

    override fun onPermissionRequest(request: PermissionRequest) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            request.grant(request.resources)
        }
    }

    override fun onCloseWindow(window: WebView?) {
        closeDialogClosure.invoke()
        super.onCloseWindow(window)
    }

    override fun onCreateWindow(
        view: WebView,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message,
    ): Boolean {
        this.resultMessage = resultMsg
        this.isDialog.invoke(isDialog)
        val transport = resultMsg.obj as WebView.WebViewTransport
        this.window = WebView(VuukleAndroidUtil.getActivity())
        VuukleWebViewConfigurationHelper.configure(this.window!!)
        window!!.webChromeClient = this
        window!!.webViewClient = VuukleWebViewClient(identifier, actionListener, openPopupCallback)
        transport.webView = this.window
        resultMsg.sendToTarget()
        return true
    }

    fun recycleWindow(): Boolean {
        val isWindow = window != null
        window?.destroy()
        window = null
        return isWindow
    }
}