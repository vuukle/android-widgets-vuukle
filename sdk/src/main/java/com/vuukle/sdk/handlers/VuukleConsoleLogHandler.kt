package com.vuukle.sdk.handlers

import android.annotation.SuppressLint
import android.util.Log
import com.vuukle.sdk.constants.logger.LoggerConstants
import com.vuukle.sdk.utils.VuukleManagerUtil

class VuukleConsoleLogHandler(val identifier: Int) {

    @SuppressLint("LongLogTag")
    fun handle(consoleMessage: String, sourceId: String) {
        Log.i("ecececrevvrv", consoleMessage)
//        when {
//            isLogoutAction(consoleMessage) -> VuukleManagerUtil.getActionManager()?.logout()
//            isSSOSignIn(consoleMessage) -> VuukleManagerUtil.getActionManager()?.onSSOSigiIn(identifier)
//            else -> Log.i(LoggerConstants.VUUKLE_LOGGER, consoleMessage)
//        }
    }

    private fun isLogoutAction(text: String) = text.contains("logout-clicked")
    private fun isSSOSignIn(text: String) = text.contains("sso-sign-in")
}