package com.vuukle.sdk.model

open class VuukleEvent {

    class YouMindLikeClickEvent(
        val url: String
    ): VuukleEvent()

    class TownTalkClickEvent(
        val url: String
    ): VuukleEvent()
}