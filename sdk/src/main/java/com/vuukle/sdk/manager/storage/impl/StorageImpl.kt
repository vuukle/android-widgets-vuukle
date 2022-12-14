package com.vuukle.sdk.manager.storage.impl

import android.content.Context
import android.content.SharedPreferences
import com.vuukle.sdk.manager.storage.StorageManager
import com.vuukle.sdk.utils.VuukleAndroidUtil


class StorageImpl() : StorageManager {

    companion object {
        const val vuukleTokenKey = "vuukle_token_key"
    }

    private fun getSharedPreferences(): SharedPreferences {
        // Create the EncryptedSharedPreferences
        return VuukleAndroidUtil.getActivity()
            .getSharedPreferences("secret_shared_prefs", Context.MODE_PRIVATE)
    }

    override fun putData(key: String, value: String) {
        val sharedPrefsEditor = getSharedPreferences().edit()
        sharedPrefsEditor?.putString(key, value)
        sharedPrefsEditor?.apply()
    }

    override fun putData(key: String, value: Int) {
        val sharedPrefsEditor = getSharedPreferences().edit()
        sharedPrefsEditor?.putInt(key, value)
        sharedPrefsEditor?.apply()
    }

    override fun putData(key: String, value: Boolean) {
        val sharedPrefsEditor = getSharedPreferences().edit()
        sharedPrefsEditor?.putBoolean(key, value)
        sharedPrefsEditor?.apply()
    }

    override fun putData(key: String, value: Float) {
        val sharedPrefsEditor = getSharedPreferences().edit()
        sharedPrefsEditor?.putFloat(key, value)
        sharedPrefsEditor?.apply()
    }

    override fun saveVuukleToken(vuukleToken: String) {
        val sharedPrefsEditor = getSharedPreferences().edit()
        sharedPrefsEditor?.putString(vuukleTokenKey, vuukleToken)
        sharedPrefsEditor?.apply()
    }

    override fun getVuukleToken(): String?  = getSharedPreferences().getString(vuukleTokenKey,"")

    override fun getBooleanData(key: String): Boolean = getSharedPreferences().getBoolean(key, false)

    override fun getFloatData(key: String): Float = getSharedPreferences().getFloat(key, 0f)

    override fun getIntData(key: String): Int = getSharedPreferences().getInt(key, 0)

    override fun getStringData(key: String): String? = getSharedPreferences().getString(key, "")
}