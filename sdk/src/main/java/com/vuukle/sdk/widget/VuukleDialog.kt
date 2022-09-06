package com.vuukle.sdk.widget

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.webkit.CookieSyncManager
import android.webkit.WebView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.RelativeLayout.*
import com.vuukle.sdk.clients.VuukleWebChromeClient
import com.vuukle.sdk.clients.VuukleWebViewClient
import com.vuukle.sdk.constants.logger.LoggerConstants
import com.vuukle.sdk.helpers.VuukleWebViewConfigurationHelper
import com.vuukle.sdk.listeners.VuukleActionListener
import com.vuukle.sdk.listeners.WebViewStateListener
import com.vuukle.sdk.manager.auth.AuthManager
import com.vuukle.sdk.manager.storage.StorageManager
import com.vuukle.sdk.manager.storage.WebStorageManager
import com.vuukle.sdk.manager.storage.impl.WebStorageManagerImpl
import com.vuukle.sdk.manager.url.VuukleViewManager
import com.vuukle.sdk.utils.VuukleAndroidUtil
import com.vuukle.sdk.utils.VuukleManagerUtil

class VuukleDialog(
    private val identifier: Int,
    private val storageManager: StorageManager,
    private val vuukleWebChromeClient: VuukleWebChromeClient,
    private val actionListener: VuukleActionListener
) : WebViewStateListener {

    private var dialog: AlertDialog? = null
    private var isOpened = false
    private var wrapper: RelativeLayout? = null
    private var progressBar: ProgressBar? = null

    var popup: WebView? = null
    private var webView: WebView? = null

    private val webStorageManager: WebStorageManager = WebStorageManagerImpl()

    fun openDialog(url: String) {
        if (isOpened) {
            onOpenPopupWindow(url)
            return
        }
        popup = WebView(VuukleAndroidUtil.getActivity())
        popup?.apply {
            VuukleWebViewConfigurationHelper.configure(this)
            this.webChromeClient = vuukleWebChromeClient
            this.webViewClient = VuukleWebViewClient(
                identifier = identifier,
                openPopupCallback = { url ->
                    this@VuukleDialog.onOpenPopupWindow(url)
                },
                webViewStateListener = this@VuukleDialog,
                actionListener = actionListener
            )
            // sync token with local storage
            val token = storageManager.getStringData(AuthManager.cookiesKey)
            if (!token.isNullOrEmpty()) {
                saveLocalStorage(token, this)
            }
            VuukleViewManager.addPopupWebView(this)
            // Load URL
            this.loadUrl(url)
        }
        initLinearLayout()
    }

    private fun saveLocalStorage(
        vuukleTokenValue: String,
        webView: WebView
    ) {
        webStorageManager.putData(webView, "vuukle_token", vuukleTokenValue)

        /*val injection =
            "javascript:window.localStorage.setItem('vuukle_token', '${vuukleTokenValue}')"
        webView.evaluateJavascript(injection) { result ->
            Log.e("testing---->>>", "saveLocalStorage $result")
            Log.e(LoggerConstants.VUUKLE_LOGGER, "saveLocalStorage $result")
        }*/
    }

    private fun initLinearLayout() {
        if (isOpened) return

        isOpened = true
        wrapper = RelativeLayout(VuukleAndroidUtil.getActivity())
        wrapper?.minimumHeight = MATCH_PARENT
        val keyboardHack = EditText(VuukleAndroidUtil.getActivity())
        keyboardHack.visibility = View.GONE
        wrapper?.addView(
            popup,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        wrapper?.addView(
            keyboardHack,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        initDialog(wrapper)
        initProgressBar()
    }

    fun showLoader(show: Boolean) {
        if (show) {
            progressBar?.visibility = View.VISIBLE
        } else {
            progressBar?.visibility = View.GONE
        }
    }

    private fun initProgressBar() {
        // add progress bar
        progressBar = ProgressBar(VuukleAndroidUtil.getActivity())
        val lp = LayoutParams(
            100,
            100
        )
        lp.addRule(CENTER_IN_PARENT, TRUE)
        lp.setMargins(50, 50, 50, 50)
        progressBar!!.layoutParams = lp
        progressBar!!.tag = "progressBar"
        if (wrapper?.findViewWithTag<ProgressBar>("progressBar") == null) {
            wrapper?.addView(progressBar)
        }
    }

    private fun initDialog(wrapper: RelativeLayout?) {
        val builder = AlertDialog.Builder(VuukleAndroidUtil.getActivity())
        builder.setNegativeButton("close") { v: DialogInterface?, l: Int ->
            Log.i(LoggerConstants.VUUKLE_LOGGER,"close")
            close()
        }
        builder.setView(wrapper)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.setOnKeyListener { _: DialogInterface?, keyCode: Int, event: KeyEvent? ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_UP) {
                back()
            }
            true
        }
        dialog?.show()
        dialog?.window?.setLayout(MATCH_PARENT, MATCH_PARENT)
    }

    fun close() {
        vuukleWebChromeClient.recycleWindow()
        VuukleManagerUtil.getAuthManager()?.saveCookies()
        dialog?.let {
            wrapper?.let {
                it.removeView(popup)
                webView?.let { webView -> it.removeView(webView) }
            }
            wrapper
            dialog?.dismiss()
            isOpened = false
            dialog = null
            popup?.destroy()
            popup = null
        }
    }

    fun onOpenPopupWindow(url: String) {
        Log.i(LoggerConstants.VUUKLE_LOGGER,"onOpenPopupWindow")
        webView = WebView(VuukleAndroidUtil.getActivity())

        webView?.apply {
            VuukleWebViewConfigurationHelper.configure(this)
            this.webChromeClient = vuukleWebChromeClient
            this.webViewClient = VuukleWebViewClient(
                identifier = identifier,
                openPopupCallback = { url ->
                    this@VuukleDialog.onOpenPopupWindow(url)
                },
                webViewStateListener = this@VuukleDialog
            )
            this.loadUrl(url)
        }

        this.wrapper?.addView(
            webView,
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    override fun onPageFinishLoad(url: String?) {
        CookieSyncManager.getInstance().sync();
        showLoader(false)
    }

    fun back() {
        if (webView != null) {
            vuukleWebChromeClient.recycleWindow()
            wrapper?.removeView(webView)
            webView = null
        } else if (popup?.canGoBack() == true) {
            popup?.goBack()
        } else {
            close()
        }
    }
}