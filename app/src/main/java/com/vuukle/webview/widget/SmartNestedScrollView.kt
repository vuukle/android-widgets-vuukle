package com.vuukle.webview.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.widget.NestedScrollView

class SmartNestedScrollView : NestedScrollView {
    var slop = 0
    val mInitialMotionX = 0.toFloat()
    val mInitialMotionY = 0.toFloat()


    var xDistance = 0.toFloat()
    var yDistance = 0.toFloat()
    var lastX = 0.toFloat()
    var lastY = 0.toFloat()

    constructor(context: Context) : super(context) {
        init(context)
    }

    fun init(context: Context) {
        val config = ViewConfiguration.get(context)
        slop = config.scaledEdgeSlop
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.x
        val y = ev.y
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                yDistance = 0f
                xDistance = yDistance
                lastX = ev.x
                lastY = ev.y

                // This is very important line that fixes
                computeScroll()
            }
            MotionEvent.ACTION_MOVE -> {
                val curX = ev.x
                val curY = ev.y
                xDistance += Math.abs(curX - lastX)
                yDistance += Math.abs(curY - lastY)
                lastX = curX
                lastY = curY

                if (xDistance > yDistance) {
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}