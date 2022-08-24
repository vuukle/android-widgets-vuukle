package com.vuukle.webview

import android.app.Application
import com.vuukle.sdk.constants.keys.VuukleKeys

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        VuukleKeys.apply {
            setPublicKey("664e0b85-5b2c-4881-ba64-3aa9f992d01c")
            setPrivateKey("bd3a64e4-7e19-46b2-ae4d-a2a0adc72cdf")
        }
    }
}