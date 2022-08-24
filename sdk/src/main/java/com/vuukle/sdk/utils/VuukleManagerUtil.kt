package com.vuukle.sdk.utils

import com.vuukle.sdk.manager.VuukleActionManager
import com.vuukle.sdk.manager.auth.AuthManager
import com.vuukle.sdk.manager.auth.FacebookManager
import com.vuukle.sdk.manager.url.UrlManager

object VuukleManagerUtil {

    private var authManager: AuthManager? = null
    private var urlManager: UrlManager? = null
    private var facebookManager: FacebookManager? = null
    private var actionManager: VuukleActionManager? = null

    fun getAuthManager() = authManager
    fun getUrlManager() = urlManager
    fun getFacebookManager() = facebookManager
    fun getActionManager() = actionManager

    fun init() {
        authManager = AuthManager()
        urlManager = UrlManager()
        facebookManager = FacebookManager()
        facebookManager?.init()
        actionManager = VuukleActionManager()
    }

    fun destroy() {
        authManager = null
        urlManager?.destroy()
        urlManager = null
        facebookManager?.destroy()
        facebookManager = null
        actionManager = null
    }
}