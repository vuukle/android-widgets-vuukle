package com.vuukle.sdk.manager.logger

import android.os.Environment
import android.util.Log
import com.vuukle.sdk.constants.logger.LoggerConstants
import java.io.File

object LoggerManager {

    fun writeFile(filename: String) {
        val logFile = File(filename);
        try {
            val process: Process =
                Runtime.getRuntime().exec("logcat AndroidRuntime:E *:S -f $logFile")
        } catch (e: Exception) {
            Log.i(LoggerConstants.VUUKLE_LOGGER, "error $e")
        }
    }

    fun getLoggerFiles() {
        val path: String = Environment.getExternalStorageDirectory().toString() + "/Pictures"
        Log.d("Files", "Path: $path")
        val directory = File(path)
        val files = directory.listFiles()
        Log.d("Files", "Size: " + files.size)
        for (i in files.indices) {

            Log.d("Files", "FileName:" + files[i].name)
        }
    }
}