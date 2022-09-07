package com.vuukle.sdk.manager.auth

import android.net.Uri
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.vuukle.sdk.exeptions.AuthorizationException
import com.vuukle.sdk.helpers.UrlHelper
import com.vuukle.sdk.utils.VuukleAndroidUtil
import com.vuukle.sdk.utils.VuukleManagerUtil

class FacebookManager {

    companion object {
        var fbProcessRuning = false
    }

    var mFacebookCallbackManager: CallbackManager? = null
    var fbShareDialog: ShareDialog? = null

    fun init() {

        this.mFacebookCallbackManager = CallbackManager.Factory.create()

        fbShareDialog = ShareDialog(VuukleAndroidUtil.getActivity())
        fbShareDialog!!.registerCallback(mFacebookCallbackManager, object :
            FacebookCallback<Sharer.Result?> {

            override fun onSuccess(result: Sharer.Result?) {
                fbProcessRuning = false
            }

            override fun onCancel() {
                Log.i("FacebookManager--->>>", "onCancel")
                VuukleManagerUtil.getActionManager()?.recycleWindow()
                VuukleManagerUtil.getActionManager()
                    ?.sendError(AuthorizationException("Facebook share canceled"))
                fbProcessRuning = false
            }

            override fun onError(error: FacebookException) {
                Log.i("FacebookManager--->>>", "onError ${error.message}")
                VuukleManagerUtil.getActionManager()?.recycleWindow()
                VuukleManagerUtil.getActionManager()
                    ?.sendError(AuthorizationException(error.message.toString()))
                fbProcessRuning = false
            }
        })

        LoginManager.getInstance().registerCallback(mFacebookCallbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    loginResult?.accessToken?.let {
                        VuukleManagerUtil.getAuthManager()?.loginViaFacebook(it.token) { token ->
                            Log.i("FacebookManager--->>>", "tokenoooo $token")

                            if (token != null) {
                                VuukleManagerUtil.getAuthManager()
                                    ?.setAuthorizationTokenForVuukle(token)
                                VuukleManagerUtil.getActionManager()?.reloadAndSave()
                            } else {
                                VuukleManagerUtil.getActionManager()
                                    ?.sendError(AuthorizationException("Can not login facebook."))
                            }
                        }
                    } ?: run {
                        Log.i("FacebookManager--->>>", "run")

                        VuukleManagerUtil.getActionManager()
                            ?.sendError(AuthorizationException("Can not login facebook."))
                    }
                    fbProcessRuning = false
                }

                override fun onCancel() {
                    VuukleManagerUtil.getActionManager()?.recycleWindow()
                    VuukleManagerUtil.getActionManager()
                        ?.sendError(AuthorizationException("Facebook login canceled."))
                    fbProcessRuning = false
                }

                override fun onError(exception: FacebookException) {
                    VuukleManagerUtil.getActionManager()?.recycleWindow()
                    VuukleManagerUtil.getActionManager()
                        ?.sendError(AuthorizationException(exception.message.toString()))
                    fbProcessRuning = false
                }
            })
    }

    fun shareByFacebook(url: String) {

        val queryData = UrlHelper.getQueryData(url)
        val contentBuilder = ShareLinkContent.Builder()
        if (queryData.containsKey("u")) {
            contentBuilder.setContentUrl(Uri.parse(queryData["u"]))
        }
        if (queryData.containsKey("quote")) {
            contentBuilder.setQuote(queryData["quote"])
        }
        fbShareDialog?.show(contentBuilder.build())
        fbProcessRuning = true
    }

    /**
     * TODO: Test way for facebook login after geeting vuukle token
     */
    fun mockFacebookLogin(){
        VuukleManagerUtil.getAuthManager()
            ?.setAuthorizationTokenForVuukle("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9lbWFpbGFkZHJlc3MiOiJuYXIuZGFsbEBtYWlsLnJ1IiwiaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvd3MvMjAwNS8wNS9pZGVudGl0eS9jbGFpbXMvbmFtZSI6Ik5hcmVrIERhbGxha3lhbiIsIlZ1dWtsZUNsYWltVHlwZXMuQXBpS2V5IjoiZDZjYmU4MWEtZWRmMy00MjYwLWFhODQtMDYwZjIxOTdlMGY0IiwiVnV1a2xlQ2xhaW1UeXBlcy5BdmF0YXIiOiJodHRwczovL2ltYWdlLnZ1dWtsZS5jb20vZDZjYmU4MWEtZWRmMy00MjYwLWFhODQtMDYwZjIxOTdlMGY0IiwiVnV1a2xlQ2xhaW1UeXBlcy5QYXNzd29yZEVudGVyZWQiOiIxIiwiaXNzIjoiVnV1a2xlQ29yZSIsImF1ZCI6IlZ1dWtsZUNvcmUifQ.A2llXu8_Yb3Bw7PrEKGk3lP5c3YEF0UTmtOsm8Q56og")
        VuukleManagerUtil.getActionManager()?.reloadAndSave()
    }

    fun loginFacebook() {

        if (fbProcessRuning) return

        fbProcessRuning = true

        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }

        LoginManager.getInstance().logInWithReadPermissions(
            VuukleAndroidUtil.getActivity(),
            mFacebookCallbackManager!!,
            listOf("public_profile", "email")
        )
    }

    fun logout() {
        LoginManager.getInstance().logOut()
    }

    fun destroy() {
        LoginManager.getInstance().unregisterCallback(mFacebookCallbackManager)
    }
}