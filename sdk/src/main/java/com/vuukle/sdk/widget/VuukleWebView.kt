package com.vuukle.sdk.widget

import android.content.Context
import android.view.MotionEvent
import android.webkit.WebView

class VuukleWebView(context: Context) : WebView(context) {

    private var onScrollChangeListener: ((l: Int, t: Int, oldl: Int, oldt: Int) -> Unit)? = null
    private var onTouchListener: ((ev: MotionEvent?) -> Unit)? = null
    private var onOverScrolledListener: ((scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) -> Unit)? = null

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        onScrollChangeListener?.invoke(l, t, oldl, oldt)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        onOverScrolledListener?.invoke(scrollX, scrollY, clampedX, clampedY)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        onTouchListener?.invoke(ev)
        return super.onTouchEvent(ev)
    }

    fun setOnScrollChangeListener(l: (l: Int, t: Int, oldl: Int, oldt: Int) -> Unit) {
        onScrollChangeListener = l
    }

    fun setOnTouchListener(l: (ev: MotionEvent?) -> Unit) {
        onTouchListener = l
    }

    fun setOnOverScrolledListener(l: (scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) -> Unit) {
        onOverScrolledListener = l
    }
}