package com.vuukle.sdk.utils

import androidx.fragment.app.FragmentActivity
import java.lang.ref.WeakReference

object VuukleAndroidUtil {

    private lateinit var activity: WeakReference<FragmentActivity>

    fun setActivity(activity: FragmentActivity) {
        this.activity = WeakReference(activity)
    }

    fun getActivity() = activity.get()!!
}