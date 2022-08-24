package com.vuukle.sdk.impl

import android.util.Log
import android.webkit.CookieManager
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.vuukle.sdk.VuukleManager
import com.vuukle.sdk.clients.VuukleWebChromeClient
import com.vuukle.sdk.clients.VuukleWebViewClient
import com.vuukle.sdk.constants.logger.LoggerConstants
import com.vuukle.sdk.exeptions.VuukleException
import com.vuukle.sdk.helpers.VuukleWebViewConfigurationHelper
import com.vuukle.sdk.listeners.SSOEventListener
import com.vuukle.sdk.listeners.VuukleActionListener
import com.vuukle.sdk.listeners.VuukleErrorListener
import com.vuukle.sdk.listeners.VuukleEventListener
import com.vuukle.sdk.manager.storage.StorageImpl
import com.vuukle.sdk.manager.url.VuukleViewManager
import com.vuukle.sdk.model.VuukleEvent
import com.vuukle.sdk.utils.ResultLauncherUtil
import com.vuukle.sdk.utils.VuukleManagerUtil
import com.vuukle.sdk.widget.VuukleDialog
import com.vuukle.sdk.widget.VuukleView
import java.util.*

class VuukleManagerImpl(val lifecycleOwner: LifecycleOwner) : VuukleManager, VuukleActionListener {

    // Identifier
    private val identifier = hashCode()

    // Managers
    private var viewManager: VuukleViewManager? = VuukleViewManager

    // Inner Callbacks
    private val openPopupCallback: (String) -> Unit = { url ->
        Log.i(LoggerConstants.VUUKLE_LOGGER,"openPopupCallback")
        this.onOpenPopupWindow(url)
    }
    private val webChromeClient = VuukleWebChromeClient(identifier, this, openPopupCallback)
    private val popupDialog = VuukleDialog(identifier, StorageImpl(), webChromeClient, this)

    // Listeners
    private var ssoEventListener: SSOEventListener? = null
    private var errorListener: VuukleErrorListener? = null
    private var eventListener: VuukleEventListener? = null

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            VuukleManagerUtil.getActionManager()?.addObserver(this@VuukleManagerImpl)
        }

        override fun onPause(owner: LifecycleOwner) {
            VuukleManagerUtil.getAuthManager()?.saveCookies()
            super.onPause(owner)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            onDestroy()
            super.onDestroy(owner)
        }
    }

    init {
        // Add lifecycleObserver
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }

    override fun load(view: VuukleView, url: String) {
        val vieIdentifier = UUID.randomUUID().toString()
        VuukleManagerUtil.getUrlManager()?.addUrl(vieIdentifier, url)
        Log.i(LoggerConstants.VUUKLE_LOGGER, "viewIdentifier = $vieIdentifier")
        viewManager?.addVuukleView(vieIdentifier, view)
        Log.i(LoggerConstants.VUUKLE_LOGGER,"VuukleView added into viewManager")
        loadContent(vieIdentifier)
    }

    private fun loadContent(vieIdentifier: String) {
        val vuukleView = viewManager?.getView(vieIdentifier) ?: return
        // Configure WebView s
        vuukleView.webView.webChromeClient = webChromeClient
        vuukleView.webView.webViewClient = VuukleWebViewClient(this.identifier, this)
        VuukleWebViewConfigurationHelper.configure(vuukleView.webView)
        // configure cookies
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.allowFileSchemeCookies()
        VuukleManagerUtil.getUrlManager()?.getUrl(vieIdentifier)?.let {
            vuukleView.webView.loadUrl(it)
        }
        Log.i(LoggerConstants.VUUKLE_LOGGER, "VuukleView content loaded")
        onReloadAndRestore()
    }

    override fun setEventListener(listener: VuukleEventListener) {
        eventListener = listener
    }

    override fun onOpenPopupWindow(url: String) {
        popupDialog.openDialog(url)
    }

    override fun onReloadAndRestore() {
        viewManager?.reloadAll()
        VuukleManagerUtil.getAuthManager()?.restoreCookies()
    }

    override fun onReloadAndSave() {
        viewManager?.reloadAll()
        Thread{
            VuukleManagerUtil.getAuthManager()?.saveCookies()
        }.start()
    }

    override fun onSendError(exception: VuukleException) {
        errorListener?.onError(exception)
    }

    override fun onRecycleWindow() {
        webChromeClient.recycleWindow()
    }

    override fun addCustomSSOEventListener(listener: SSOEventListener) {
        this.ssoEventListener = listener
    }

    override fun loginBySSO(email: String, userName: String) {
        // Login user
        VuukleManagerUtil.getAuthManager()?.login(email, userName)
        // Clear browser history
        viewManager?.clearHistory()
        // Logout facebook
        VuukleManagerUtil.getFacebookManager()?.logout()
        // Reload WebView using urlManager
        viewManager?.fetchReload()
    }

    override fun addErrorListener(listener: VuukleErrorListener) {
        this.errorListener = listener
    }

    override fun onSSOSignIn() {
        ssoEventListener?.onSignInClicked()
    }

    override fun logout() {
        // Notify all to logout
        VuukleManagerUtil.getActionManager()?.logout()
    }

    override fun onEvent(event: VuukleEvent) {
        if(eventListener != null){
            eventListener?.onNewEvent(event)
        }else{
            when(event){
                is VuukleEvent.YouMindLikeClickEvent -> {
                    Log.i(LoggerConstants.VUUKLE_LOGGER,"YouMindLikeClickEvent")
                    onOpenPopupWindow(event.url)
                }
                is VuukleEvent.TownTalkClickEvent -> {
                    Log.i(LoggerConstants.VUUKLE_LOGGER,"TownTalkClickEvent")
                    onOpenPopupWindow(event.url)
                }
            }
        }
    }

    override fun onLogout() {
        // Logout user
        VuukleManagerUtil.getAuthManager()?.logout()
        // Clear all cookies
        CookieManager.getInstance().removeAllCookie()
        // Clear history
        viewManager?.clearHistory()
        // Reload WebView using urlManager
        viewManager?.fetchReload()
    }

    fun onDestroy() {
        VuukleManagerUtil.getAuthManager()?.saveCookies()
        lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        viewManager?.destroy()
        viewManager = null
        ResultLauncherUtil.destroy()
        VuukleManagerUtil.destroy()
    }
}