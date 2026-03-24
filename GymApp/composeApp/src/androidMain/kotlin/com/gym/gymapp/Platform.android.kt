package com.gym.gymapp

import android.content.Intent
import android.net.Uri
import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val baseUrl: String = "http://192.168.1.50:3000"
    override val cacheDir: String? get() = context?.cacheDir?.absolutePath
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
    override fun exitApp() {
        onExitRequest?.invoke()
    }

    override fun openUrl(url: String) {
        onOpenUrlRequest?.invoke(url)
    }

    companion object {
        var onExitRequest: (() -> Unit)? = null
        var onOpenUrlRequest: ((String) -> Unit)? = null
        var context: android.content.Context? = null
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()