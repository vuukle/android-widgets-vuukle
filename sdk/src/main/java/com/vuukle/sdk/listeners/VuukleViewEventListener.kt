package com.vuukle.sdk.listeners

import android.view.MotionEvent
import com.vuukle.sdk.widget.VuukleView

interface VuukleViewEventListener {

    fun onScrollChange(view: VuukleView, l: Int, t: Int, oldl: Int, oldt: Int) {/* Ignore */
    }

    fun onTouch(view: VuukleView, ev: MotionEvent?) {/* Ignore */
    }

    fun onOverScroll(view: VuukleView, scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {/* Ignore */
    }
}