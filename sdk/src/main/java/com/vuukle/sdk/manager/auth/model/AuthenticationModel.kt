package com.vuukle.sdk.manager.auth.model

import org.json.JSONObject

data class AuthenticationModel(
    val email: String,
    val publicKey: String,
    val signature: String,
    val username: String,
) {

    fun toJson(): String {
        val jsonObject = JSONObject()
        jsonObject.put("email", email)
        jsonObject.put("public_key", publicKey)
        jsonObject.put("signature", signature)
        jsonObject.put("username", username)
        return jsonObject.toString();
    }
}