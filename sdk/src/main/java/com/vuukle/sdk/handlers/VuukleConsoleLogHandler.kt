package com.vuukle.sdk.handlers

import android.annotation.SuppressLint

class VuukleConsoleLogHandler(
    val identifier: Int,
    private val onSignInClicked: () -> Unit
) {

    @SuppressLint("LongLogTag")
    fun handle(consoleMessage: String, sourceId: String) {
        if (consoleMessage == "signin-clicked") {
            onSignInClicked.invoke()
        }
    }

    private fun isLogoutAction(text: String) = text.contains("logout-clicked")
    private fun isSSOSignIn(text: String) = text.contains("sso-sign-in")
}