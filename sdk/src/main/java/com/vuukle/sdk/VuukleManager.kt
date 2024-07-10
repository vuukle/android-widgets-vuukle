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

            if (VuukleAndroidUtil.getActivity().lifecycle.currentState != Lifecycle.State.INITIALIZED &&
                VuukleAndroidUtil.getActivity().lifecycle.currentState != Lifecycle.State.CREATED
            ) {
                throw VuukleException("Vuukle manager must be initialized in activity onCreate function")
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
    fun logout()

    class Builder(val lifecycleOwner: LifecycleOwner) {

        fun build(): VuukleManager {

            if (!ResultLauncherUtil.isInitialized()) {
                throw VuukleException("Vuukle manager must be initialized in activity onCreate function: use VuukleManager.init(FragmentActivity)")
            }

            return VuukleManagerImpl(lifecycleOwner)
        }
    }
}