package com.vuukle.sdk.listeners

interface WebViewStateListener {
    fun onPageFinishLoad(url: String?)
}