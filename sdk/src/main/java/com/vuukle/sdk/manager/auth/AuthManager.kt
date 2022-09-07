package com.vuukle.sdk.manager.auth

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Base64.DEFAULT
import android.util.Log
import android.webkit.WebStorage
import android.webkit.WebView
import com.vuukle.sdk.constants.keys.VuukleKeys
import com.vuukle.sdk.constants.logger.LoggerConstants
import com.vuukle.sdk.manager.auth.model.AuthenticationModel
import com.vuukle.sdk.manager.network.BaseApiClient
import com.vuukle.sdk.manager.storage.impl.StorageImpl
import com.vuukle.sdk.manager.storage.impl.WebStorageManagerImpl
import com.vuukle.sdk.manager.url.VuukleViewManager
import com.vuukle.sdk.utils.VuukleManagerUtil
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class AuthManager {

    companion object {
        const val tokenKey = "token"
    }

    private val privateKey = VuukleKeys.getPrivateKey()
    private val publicKey = VuukleKeys.getPublicKey()
    private val storageManager = StorageImpl()
    private val webStorageManager = WebStorageManagerImpl()

    private var viewManager: VuukleViewManager? = VuukleViewManager

    fun loginViaFacebook(fbToken: String, onResult: (String?) -> Unit) {

        Thread {
            val apiClient = BaseApiClient()
            val url = "https://cdn.vuukle.com/login/auth/facebookLogin"
            val params = LinkedHashMap<String, String>()
            params["token"] = fbToken
            val response = apiClient.get(url, params)

            if (response == null) {
                Handler(Looper.getMainLooper()).post {
                    onResult.invoke(null)
                }
                return@Thread
            }

            val jsonObject = JSONObject(response)

            if (jsonObject.has("vuukleToken")) {
                Handler(Looper.getMainLooper()).post {
                    onResult.invoke(jsonObject.getString("vuukleToken").toString())
                }
            } else {
                Handler(Looper.getMainLooper()).post {
                    onResult.invoke(null)
                }
            }
        }.start()
    }

    /**
     * Login by user email and Name
     */
    @Synchronized
    fun login(email: String?, userName: String?): Boolean {

        if (email.isNullOrEmpty() || userName.isNullOrEmpty()) return false

        val authModel = generateAuthenticationModel(email, userName)
        val authJsonString = authModel.toJson()

        val authToken = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getEncoder().encodeToString(authJsonString.toByteArray(Charsets.UTF_8))
        } else {
            android.util.Base64.encodeToString(authJsonString.toByteArray(Charsets.UTF_8), DEFAULT)
        }
        storageManager.putData(tokenKey, authToken)
        return true
    }

    /**
     * Returns user login state
     */
    fun isLoggedIn(): Boolean {
        val token = storageManager.getStringData(tokenKey)
        return !(token.isNullOrEmpty() || token.isBlank())
    }

    /**
     * Logout
     */
    fun logout() {
        storageManager.putData(tokenKey, "")
        removeStorageData()
    }

    /**
     * Get user token
     */
    fun getToken(): String? {
        return storageManager.getStringData(tokenKey)
    }

    private fun generateAuthenticationModel(email: String, userName: String): AuthenticationModel {

        return AuthenticationModel(
            email = email,
            username = userName,
            publicKey = publicKey,
            signature = generateSignature(email)
        )
    }

    private fun generateSignature(email: String): String {

        val signatureString = email.plus("-").plus(privateKey)
        val upper = encodeSignature(signatureString).uppercase(Locale.ROOT)
        return upper
    }

    private fun encodeSignature(stringHash: String): String {

        var token = ""

        try {
            val md = MessageDigest.getInstance("SHA-512")
            val bytes = md.digest(stringHash.toByteArray(StandardCharsets.UTF_8))
            token = bytes.map { Integer.toHexString(0xFF and it.toInt()) }
                .map { if (it.length < 2) "0$it" else it }
                .fold("", { acc, d -> acc + d })
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }

        return token
    }

    fun restoreVuukleToken() {
        if (isLoggedIn()) return
        val vuukleTokenString = storageManager.getVuukleToken()
        Log.i("testing--->", "restoreVuukleToken $vuukleTokenString")
        Log.i(LoggerConstants.VUUKLE_LOGGER, "restoreVuukleToken $vuukleTokenString")
        setAuthorizationTokenForVuukle(vuukleTokenString!!)
    }

    fun saveVuukleToken() {
        val urls = HashSet(VuukleManagerUtil.getUrlManager()?.getAllUrls())
        if (urls.isEmpty()) return
        Log.i(LoggerConstants.VUUKLE_LOGGER, "vuukle urls size = ${urls.size}")
        urls.forEach { _ ->
            findTokenFromWebViewLocalStorage { vuukleToken ->
                if (vuukleToken != null) {
                    storageManager.saveVuukleToken(vuukleToken)
                    Log.i("testing--->", vuukleToken.toString())
                    Log.i(LoggerConstants.VUUKLE_LOGGER, "vuukle token = $vuukleToken")
                }
            }
        }
    }

    // "javascript:window.localStorage.getItem('vuukle_token')"
    private fun findTokenFromWebViewLocalStorage(onResult: (String?) -> Unit) {
        Log.i("testing--->>>", "findTokenFromWebViewLocalStorage")
        viewManager?.getAllViews()?.map { viewEntry ->
            Handler(Looper.getMainLooper()).post {
                val result =
                    webStorageManager.getStringData(viewEntry.value.webView, "vuukle_token")
                onResult(result ?: "")
            }
            return@map
        }
    }

    private fun removeStorageData() {
        storageManager.saveVuukleToken("")
        WebStorage.getInstance().deleteAllData()
    }

    fun setAuthorizationTokenForVuukle(vuukleToken: String) {
        val urls = VuukleManagerUtil.getUrlManager()?.getAllUrls()
        if (!urls.isNullOrEmpty()) {
            viewManager?.getAllViews()?.map { viewEntry ->
                saveToWebLocalStorage(
                    vuukleToken,
                    viewEntry.value.webView
                )
            }
            viewManager?.getAllPopupViews()?.map { popupWebView ->
                saveToWebLocalStorage(
                    vuukleToken,
                    popupWebView
                )
            }
        }
    }

    private fun saveToWebLocalStorage(
        vuukleTokenValue: String,
        webView: WebView
    ) {
        webStorageManager.putData(webView, "vuukle_token", vuukleTokenValue)
    }
}