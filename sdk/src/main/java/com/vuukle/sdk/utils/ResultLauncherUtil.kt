package com.vuukle.sdk.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.ValueCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.vuukle.sdk.constants.logger.LoggerConstants

object ResultLauncherUtil {

    private var resultLauncher: ActivityResultLauncher<Intent>? = null
    private var uploadMessage: ValueCallback<Array<Uri>>? = null

    fun init() {
        registerOnActivityResultListener()
    }

    fun registerValueCallback(uploadMessage: ValueCallback<Array<Uri>>?) {
        this.uploadMessage = uploadMessage
    }

    fun isInitialized(): Boolean {
        if (resultLauncher == null) return false
        return true
    }

    private fun registerOnActivityResultListener() {

        resultLauncher = VuukleAndroidUtil.getActivity().registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val result =
                if (it == null || it.data?.data == null || it.resultCode != Activity.RESULT_OK) null else arrayOf(
                    it.data!!.data!!)
            result?.let {
                uploadMessage?.onReceiveValue(result)
            } ?: kotlin.run {
                uploadMessage?.onReceiveValue(arrayOf())
            }
            uploadMessage = null
        }
    }

    fun openFileChooser(intent: Intent, filePathCallback: ValueCallback<Array<Uri>>?) {
        this.uploadMessage = filePathCallback
        resultLauncher?.launch(intent)
    }

    fun destroy() {
        resultLauncher?.unregister()
        resultLauncher = null
        uploadMessage = null
    }
}