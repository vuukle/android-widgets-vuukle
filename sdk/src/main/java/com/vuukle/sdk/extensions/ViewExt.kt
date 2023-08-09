package com.vuukle.sdk.extensions

import android.view.View
import android.view.ViewGroup

fun View?.removeSelf() {
    this ?: return
    val parentView = parent as? ViewGroup ?: return
    parentView.removeView(this)
}