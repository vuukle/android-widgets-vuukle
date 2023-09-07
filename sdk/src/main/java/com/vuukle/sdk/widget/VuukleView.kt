package com.vuukle.sdk.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import com.vuukle.sdk.listeners.VuukleViewEventListener


class VuukleView : RelativeLayout {

    private var eventListener: VuukleViewEventListener? = null
    lateinit var webView: VuukleWebView

    init {
        initParentWebView()
    }

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet?) : super(ctx, attrs)

    private fun initParentWebView() {

        webView = VuukleWebView(context)
        webView.settings.domStorageEnabled = true
        webView.isScrollbarFadingEnabled = false
        val lp = LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        webView.layoutParams = lp
        this.addView(webView)
        initListeners()
    }

    private fun initListeners() {
        webView.setOnOverScrolledListener { scrollX, scrollY, clampedX, clampedY ->
            this.eventListener?.onOverScroll(this, scrollX, scrollY, clampedX, clampedY)
        }
        webView.setOnScrollChangeListener { l, t, oldl, oldt ->
            this.eventListener?.onScrollChange(this, l, t, oldl, oldt)
        }
        webView.setOnTouchListener { ev ->
            this.eventListener?.onTouch(this, ev)
        }
    }

    fun setVuukleViewEventListener(listener: VuukleViewEventListener) {
        this.eventListener = listener
    }

    fun destroy() {
        eventListener = null
        webView.destroy()
    }
}