package com.vuukle.sdk

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.vuukle.sdk.constants.keys.VuukleKeys
import com.vuukle.sdk.exeptions.VuukleException
import com.vuukle.sdk.impl.VuukleManagerImpl
import com.vuukle.sdk.listeners.SSOEventListener
import com.vuukle.sdk.listeners.VuukleErrorListener
import com.vuukle.sdk.listeners.VuukleEventListener
import com.vuukle.sdk.listeners.VuukleEventPopupListener
import com.vuukle.sdk.utils.ResultLauncherUtil
import com.vuukle.sdk.utils.VuukleAndroidUtil
import com.vuukle.sdk.utils.VuukleManagerUtil
import com.vuukle.sdk.widget.VuukleView

interface VuukleManager {

    // Initialize
    companion object {
        fun init(fragmentActivity: FragmentActivity) {
            VuukleKeys.checkKeys()
            VuukleAndroidUtil.setActivity(fragmentActivity)

            // Previously crashed if init() wasn't called in onCreate. Publishers like
            // Manorama legitimately init in onResume or after a network check - throwing
            // here was hostile. We now log a warning instead.
            val state = VuukleAndroidUtil.getActivity().lifecycle.currentState
            if (state != Lifecycle.State.INITIALIZED && state != Lifecycle.State.CREATED) {
                android.util.Log.w(
                    "VuukleSDK",
                    "VuukleManager.init() called outside Activity.onCreate (lifecycle=$state). " +
                    "This may lead to unexpected behavior; prefer initializing in onCreate."
                )
            }
            VuukleManagerUtil.init()
            ResultLauncherUtil.init()
        }
    }

    fun load(view: VuukleView, url: String, backgroundColor: String? = null)
    fun loginBySSO(token: String?)
    fun addErrorListener(listener: VuukleErrorListener)
    fun addCustomSSOEventListener(listener: SSOEventListener)
    fun setEventListener(listener: VuukleEventListener)
    fun setPopupListner(listener: VuukleEventPopupListener)
    fun logout()
    fun onDestroyActivity()

    class Builder(val lifecycleOwner: LifecycleOwner) {

        fun build(): VuukleManager {

            if (!ResultLauncherUtil.isInitialized()) {
                throw VuukleException("Vuukle manager must be initialized in activity onCreate function: use VuukleManager.init(FragmentActivity)")
            }

            return VuukleManagerImpl(lifecycleOwner)
        }
    }
}