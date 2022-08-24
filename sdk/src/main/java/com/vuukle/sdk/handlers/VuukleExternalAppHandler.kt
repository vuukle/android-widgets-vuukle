package com.vuukle.sdk.handlers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.vuukle.sdk.constants.Keywords
import com.vuukle.sdk.extensions.isAppInstalled
import com.vuukle.sdk.utils.VuukleAndroidUtil
import com.vuukle.sdk.utils.VuukleManagerUtil
import java.io.UnsupportedEncodingException
import java.net.URLDecoder


class VuukleExternalAppHandler() {

    fun handleExternalApp(url: String): Boolean {

        return when {
            containsKeyword(url, Keywords.WATS_APP) -> openWhatsApp(url)
            containsKeyword(url, Keywords.TELEGRAM) -> openTelegram(url)
            containsKeyword(url, Keywords.MAIL) -> openEmail(url)
            containsKeyword(url, Keywords.GOOGLE_PLAY) -> openGooglePlay(url)
            containsKeyword(url, Keywords.FACEBOOK_LOGIN) -> loginFacebook()
            containsKeyword(url, Keywords.FACEBOOK_SHARE) -> shareFacebook(url)
            else -> false
        }
    }

    private fun shareFacebook(url: String): Boolean {
        VuukleManagerUtil.getFacebookManager()?.shareByFacebook(url)
        return true
    }

    private fun loginFacebook(): Boolean {
        VuukleManagerUtil.getFacebookManager()?.loginFacebook()
        return true
    }

    private fun containsKeyword(url: String, keywords: ArrayList<String>): Boolean {
        if (keywords.isEmpty()) return false
        keywords.forEach {
            if (url.contains(it)) return true
        }
        return false
    }

    private fun openWhatsApp(url: String): Boolean {

        if (!VuukleAndroidUtil.getActivity().isAppInstalled("com.whatsapp")) return false

        val decodedurl = decodeUrl(url)

        if (Keywords.WATS_APP.isEmpty()) return false

        Keywords.WATS_APP.forEach {
            if (decodedurl.contains(it)) {
                openApp("https://api.whatsapp.com/send?text=" + decodedurl.substring(decodedurl.indexOf(
                    it) + it.length))
                return true
            }
        }

        return false
    }

    private fun openEmail(url: String): Boolean {

        val decodedUrl = decodeUrl(url)
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
            "mailto", "", null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
            decodedUrl.substring(decodedUrl.indexOf("subject=") + 8, decodedUrl.indexOf("&body")))
        emailIntent.putExtra(Intent.EXTRA_TEXT,
            decodedUrl.substring(decodedUrl.indexOf("body=") + 5))
        try {
            VuukleAndroidUtil.getActivity().startActivity(Intent.createChooser(emailIntent, null))
        } catch (ex: ActivityNotFoundException) {
            VuukleAndroidUtil.getActivity().startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.google.android.gm")))
        }
        return true
    }

    private fun openTelegram(url: String): Boolean {
        openApp(url)
        return true
    }

    private fun openGooglePlay(url: String): Boolean {
        openApp(url)
        return true
    }

    private fun decodeUrl(url: String): String {
        val decodedUrl = try {
            URLDecoder.decode(url, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return url
        }
        return decodedUrl
    }

    private fun openApp(url: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        VuukleAndroidUtil.getActivity().startActivity(intent)
    }

    private fun isAppInstalled(packageName: String, context: Context): Boolean {

        val pm: PackageManager = context.packageManager

        val packages: List<ApplicationInfo> =
            pm.getInstalledApplications(PackageManager.GET_META_DATA)

        for (packageInfo in packages) {
            Log.d("package", "Installed package :" + packageInfo.packageName)
            Log.d("package", "Source dir : " + packageInfo.sourceDir)
            Log.d("package",
                "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName))
        }

        val appInstalled: Boolean = try {
            pm.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

        return appInstalled
    }
}