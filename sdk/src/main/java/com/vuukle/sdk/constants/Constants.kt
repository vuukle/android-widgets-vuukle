package com.vuukle.sdk.constants

object Keywords {

    val WATS_APP = arrayListOf(
        "https://api.whatsapp.com/send?text=",
        "https://web.whatsapp.com/send?text=",
        "whatsapp://send?text="
    )
    val TELEGRAM = arrayListOf("tg:msg_url")
    val GOOGLE_PLAY = arrayListOf("play.google.com/store/apps/details?id=", "market://details?id=")
    val MAIL = arrayListOf("mailto:", "mailto:to")
    val FACEBOOK_LOGIN = arrayListOf("vuukle.com/login/auth/facebook")
    val FACEBOOK_SHARE = arrayListOf("facebook.com/share")
}