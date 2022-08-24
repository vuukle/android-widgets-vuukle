package com.vuukle.sdk.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager

fun String.copyToClipBoard(context: Context) {
    val clipboard: ClipboardManager? =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val clip = ClipData.newPlainText("copy", this)
    clipboard?.setPrimaryClip(clip)
}

fun Context.isAppInstalled(packageName: String): Boolean {
    val pm: PackageManager = this.packageManager
    return try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}