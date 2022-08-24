package com.vuukle.sdk.constants.keys

import com.vuukle.sdk.exeptions.VuukleKeyException

object VuukleKeys {

    private var publisherPrivateKey: String? = null
    private var publisherPublicKey: String? = null

    fun checkKeys(){
        if (publisherPrivateKey == null) {
            throw VuukleKeyException("Publisher private key is required")
        }
        if (publisherPublicKey == null) {
            throw VuukleKeyException("Publisher public key is required")
        }
    }

    fun setPrivateKey(privateKey: String) {
        publisherPrivateKey = privateKey
    }

    fun setPublicKey(publicKey: String) {
        publisherPublicKey = publicKey
    }

    fun getPrivateKey(): String {
        return publisherPrivateKey!!
    }

    fun getPublicKey(): String {
        return publisherPublicKey!!
    }
}