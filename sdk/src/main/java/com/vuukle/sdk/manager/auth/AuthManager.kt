package com.vuukle.sdk.manager.auth

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Base64.DEFAULT
import android.util.Log
import android.webkit.WebStorage
import android.webkit.WebView
import com.vuukle.sdk.BuildConfig
import com.vuukle.sdk.constants.logger.LoggerConstants
import com.vuukle.sdk.manager.auth.model.AuthenticationModel
import com.vuukle.sdk.manager.network.BaseApiClient
import com.vuukle.sdk.manager.storage.StorageImpl
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
        const val cookiesKey = "saved_cookies"
    }

    private val privateKey = BuildConfig.PUBLISHER_PRIVATE_KEY
    private val publicKey = BuildConfig.PUBLISHER_PUBLIC_KEY
    private val storageManager = StorageImpl()

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
        removeCookies()
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

    fun restoreCookies() {
        if (isLoggedIn()) return
        val vuukleTokenString = storageManager.getStringData(cookiesKey)
        Log.i("testing--->", "restoreCookies $vuukleTokenString")
        Log.i(LoggerConstants.VUUKLE_LOGGER, "restoreCookies $vuukleTokenString")
        setAuthorizationCookieForVuukle(vuukleTokenString!!)
    }

    fun saveCookies() {
        val urls = HashSet(VuukleManagerUtil.getUrlManager()?.getAllUrls())
        if (urls.isEmpty()) return
        Log.i(LoggerConstants.VUUKLE_LOGGER, "vuukle urls size = ${urls.size}")
        urls.forEach { _ ->
            findTokenFromCookies { vuukleToken ->
                if (vuukleToken != null) {
                    storageManager.putData(cookiesKey, vuukleToken)
                    Log.i("testing--->", vuukleToken.toString())
                    Log.i(LoggerConstants.VUUKLE_LOGGER, "vuukle token = $vuukleToken")
                }
            }
        }
    }

    // "javascript:window.localStorage.getItem('vuukle_token')"
    private fun findTokenFromCookies(onResult: (String?) -> Unit) {
        Log.i("testing--->>>", "findTokenFromCookies")
        viewManager?.getAllViews()?.map { viewEntry ->
            Handler(Looper.getMainLooper()).post {
                viewEntry.value.webView.evaluateJavascript(
                    "javascript:window.localStorage.getItem('vuukle_token')"
                ) { result ->
                    Log.e("testing---->>>", result)
                    onResult.invoke(result ?: "")
                }
            }
            return@map
        }
    }

    //// TODO: replaced saving data to cookie to localStorage
//    private fun findTokenFromCookies(cookie: String?): String {
//        if (cookie == null) return ""
//        val tokenKey = "vuukle_token="
//        val tokenStartIndex = cookie.indexOf(tokenKey)
//        if (tokenStartIndex == -1) return ""
//        val subStr = cookie.substring(tokenStartIndex + tokenKey.length)
//        var endIndex = subStr.indexOf(";")
//        if (endIndex == -1) endIndex = subStr.length
//        return subStr.substring(0, endIndex)
//    }

    //// TODO: replaced saving data to cookie to localStorage
//    private fun removeCookies() {
//
//        val cookieManager = CookieManager.getInstance()
//        storageManager.putData(cookiesKey, "")
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            cookieManager.removeAllCookies(null)
//            cookieManager.flush()
//        } else {
//            val cookieSyncMngr = CookieSyncManager.createInstance(VuukleAndroidUtil.getActivity())
//            cookieSyncMngr.startSync()
//            val cookieManager = CookieManager.getInstance()
//            cookieManager.removeAllCookie()
//            cookieManager.removeSessionCookie()
//            cookieSyncMngr.stopSync()
//            cookieSyncMngr.sync()
//        }
//    }
    private fun removeCookies() {
        storageManager.putData(cookiesKey, "")
        WebStorage.getInstance().deleteAllData()
    }

    fun setAuthorizationCookieForVuukle(
        tokenCookie: String
    ) {
        val urls = VuukleManagerUtil.getUrlManager()?.getAllUrls()
        if (!urls.isNullOrEmpty()) {
            viewManager?.getAllViews()?.map { viewEntry ->
                saveLocalStorage(
                    tokenCookie,
                    viewEntry.value.webView
                )
            }
            viewManager?.getAllPopupViews()?.map { popupWebView ->
                saveLocalStorage(
                    tokenCookie,
                    popupWebView
                )
            }
        }
    }

    // TODO: replaced saving data to cookie to localStorage
//    fun setAuthorizationCookieForVuukle(tokenCookie: String) {
//        val cookieManager = CookieManager.getInstance()
//        val urls = VuukleManagerUtil.getUrlManager()?.getAllUrls()
//        if (!urls.isNullOrEmpty()) {
//            urls.forEach {
//                cookieManager.setCookie(it, tokenCookie)
//            }
//        }
//    }


    private fun saveLocalStorage(
        vuukleTokenValue: String,
        webView: WebView
    ) {
        val injection = "javascript:window.localStorage.setItem('vuukle_token', '${vuukleTokenValue}')"
        webView.evaluateJavascript(injection) { result ->
            Log.e("testing---->>>", "saveLocalStorage $result")
            Log.e(LoggerConstants.VUUKLE_LOGGER, "saveLocalStorage $result")
        }
    }
}