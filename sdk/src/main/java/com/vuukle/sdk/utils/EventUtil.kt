package com.vuukle.sdk.utils

import com.vuukle.sdk.helpers.UrlHelper
import com.vuukle.sdk.model.VuukleEvent
import com.vuukle.sdk.model.VuukleEvent.TownTalkClickEvent
import com.vuukle.sdk.model.VuukleEvent.YouMindLikeClickEvent

object EventUtil {

    fun createEventByUrl(url: String): VuukleEvent? {

        val urlLower = url.lowercase()
        return when {
            urlLower.contains("external?source=emote_recommendations") -> YouMindLikeClickEvent(clarifyUrl(url))
            urlLower.contains("news.vuukle.com/u/landing?story_link=") -> TownTalkClickEvent(url)
            else -> null
        }
    }

    fun clarifyUrl(url: String): String {
        var urlLower = url.lowercase()
        if (urlLower.contains("external?source=emote_recommendations")) {
            val query = UrlHelper.getQueryData(url)
            urlLower = if (query.contains("url")) query["url"].toString() else url
        }
        return urlLower
    }
}