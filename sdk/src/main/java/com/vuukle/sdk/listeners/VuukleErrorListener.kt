package com.vuukle.sdk.listeners

import com.vuukle.sdk.exeptions.VuukleException

interface VuukleErrorListener {

    fun onError(exception: VuukleException)
}