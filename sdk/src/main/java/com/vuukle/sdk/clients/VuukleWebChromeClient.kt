package com.vuukle.sdk.clients

import android.content.Intent
import android.net.Uri
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Message
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.view.menu.ActionMenuItemView
import com.facebook.internal.Logger
import com.vuukle.sdk.handlers.VuukleConsoleLogHandler
import com.vuukle.sdk.helpers.VuukleWebViewConfigurationHelper
import com.vuukle.sdk.listeners.VuukleActionListener
import com.vuukle.sdk.utils.ResultLauncherUtil
import com.vuukle.sdk.utils.VuukleAndroidUtil

class VuukleWebChromeClient(
    private val identifier: Int,
    private val actionListener: VuukleActionListener?,
    private val openPopupCallback: ((String) -> Unit)? = null
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

    override fun onCreateWindow(
        view: WebView,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message,
    ): Boolean {
        this.window = WebView(VuukleAndroidUtil.getActivity())
        VuukleWebViewConfigurationHelper.configure(window!!)
        this.resultMessage = resultMsg
        window!!.webChromeClient = this
        window!!.webViewClient = VuukleWebViewClient(identifier, actionListener, openPopupCallback)
        val transport = resultMessage?.obj as WebView.WebViewTransport
        transport.webView = window
        resultMessage?.sendToTarget()
        return true
    }

    fun recycleWindow(): Boolean {
        val isWindow = window != null
        window?.destroy()
        window = null
        return isWindow
    }
}