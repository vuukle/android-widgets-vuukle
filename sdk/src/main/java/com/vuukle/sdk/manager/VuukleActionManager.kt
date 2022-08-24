package com.vuukle.sdk.manager

import com.vuukle.sdk.exeptions.VuukleException
import com.vuukle.sdk.listeners.VuukleActionListener
import java.util.*

class VuukleActionManager {

    private var observers = TreeMap<Int, VuukleActionListener>()

    fun onOpenPopupWindow(observer: Int, url: String) {
        if (observers.containsKey(observer))
            observers[observer]!!.onOpenPopupWindow(url)
    }

    fun reloadAndSave() {
        observers.forEach { it.value.onReloadAndSave() }
    }

    fun sendError(exception: VuukleException) {
        observers.forEach { it.value.onSendError(exception) }
    }

    fun logout() {
        observers.forEach { it.value.onLogout() }
    }

    fun onSSOSigiIn(observer: Int) {
        if (observers.containsKey(observer))
            observers[observer]!!.onSSOSignIn()
    }

    fun recycleWindow() {
        observers.forEach { it.value.onRecycleWindow() }
    }

    fun addObserver(observer: VuukleActionListener) {
        observers[observer.hashCode()] = observer
    }

    fun removeObserver(observer: VuukleActionListener) {
        if (observers.containsKey(observer.hashCode()))
            observers.remove(observer.hashCode())
    }
}