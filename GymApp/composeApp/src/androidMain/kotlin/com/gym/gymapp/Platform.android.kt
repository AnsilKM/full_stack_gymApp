package com.gym.gymapp

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val baseUrl: String = "http://192.168.1.35:3000"
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
    override fun exitApp() {
        onExitRequest?.invoke()
    }

    companion object {
        var onExitRequest: (() -> Unit)? = null
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()