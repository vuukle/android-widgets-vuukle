package com.vuukle.sdk.listeners

import com.vuukle.sdk.exeptions.VuukleException
import com.vuukle.sdk.model.VuukleEvent

interface VuukleActionListener {

    fun onOpenPopupWindow(url: String)
    fun onSSOSignIn()
    fun onLogout()
    fun onReloadAndSave()
    fun onReloadAndRestore()
    fun onRecycleWindow()
    fun onEvent(event: VuukleEvent)
    fun onSendError(exception: VuukleException)
}