package com.vuukle.sdk.manager.url

import com.vuukle.sdk.utils.VuukleManagerUtil
import java.util.*

class UrlManager {

    private var urlList = TreeMap<String, String>()

    fun addUrl(identifier: String, url: String) {
        urlList[identifier] = url
    }

    fun getUrl(identifier: String): String {

        var url = if (urlList.containsKey(identifier)) urlList[identifier]!! else "about:blank"

        if (VuukleManagerUtil.getAuthManager()?.isLoggedIn() == true) {
            val char = if (url.contains("?")) "&" else "?"
            url = url.plus("${char}sso=true&loginToken=${
                VuukleManagerUtil.getAuthManager()?.getToken()
            }")
        }

        return url
    }

    fun getAllUrls(): ArrayList<String> {
        val urls = ArrayList<String>()
        if (urlList.isNotEmpty()) {
            urls.addAll(urlList.values)
        }
        //TODO
        urls.add(".vuukle.com")
        urls.add("vuukle.com")
        urls.add("https://news.vuukle.com")
        urls.add("https://dash.vuukle.com")
        return urls
    }

    fun destroy() {
        urlList.clear()
        urlList = TreeMap()
    }
}