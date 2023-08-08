package com.vuukle.sdk.manager.url

import android.webkit.WebView
import com.vuukle.sdk.utils.VuukleManagerUtil
import com.vuukle.sdk.widget.VuukleView
import java.util.TreeMap

object VuukleViewManager {

    private var vuukleViews = TreeMap<String, VuukleView>()

    private var popUpWebViews = ArrayList<WebView>()

    fun addPopupWebView(popUpWebViews: WebView) {
        this.popUpWebViews.add(popUpWebViews)
    }

    fun addVuukleView(identifier: String, view: VuukleView) {
        vuukleViews[identifier] = view
    }

    fun getAllPopupViews(): ArrayList<WebView> = popUpWebViews
    fun getAllViews(): TreeMap<String, VuukleView> = vuukleViews

    fun getView(identifier: String): VuukleView? {
        return if (vuukleViews.containsKey(identifier)) vuukleViews[identifier] else null
    }

    fun fetchReload() {
        vuukleViews.forEach { (identifier, view) ->
            VuukleManagerUtil.getUrlManager()?.getUrl(identifier)?.let {
                view.webView.loadUrl(it)
            }
        }
    }

    fun reloadAll() {
        vuukleViews.forEach { (_, view) ->
            view.webView.reload()
        }
    }

    fun clearHistory() {
        vuukleViews.forEach { (_, view) ->
            view.webView.clearHistory()
        }
    }

    fun destroy() {
        vuukleViews.clear()
        vuukleViews = TreeMap()
    }
}