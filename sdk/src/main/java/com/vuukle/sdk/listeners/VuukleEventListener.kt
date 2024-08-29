package com.vuukle.sdk.listeners

import com.vuukle.sdk.model.VuukleEvent

interface VuukleEventListener {
    fun onNewEvent(event: VuukleEvent)
}

interface VuukleEventPopupListener {
    fun onPopupOpen(url: String)
}