package com.vuukle.sdk.extensions

fun String.formatColor(): String {
    return when {
        this.startsWith("#") -> "%23" + this.substring(1)
        this.startsWith("%23") -> this
        else -> throw IllegalArgumentException("Unsupported color format")
    }
}