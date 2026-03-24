package com.gym.gymapp

interface Platform {
    val name: String
    val baseUrl: String
    val cacheDir: String?
    fun currentTimeMillis(): Long
    fun exitApp()
    fun openUrl(url: String)
}

expect fun getPlatform(): Platform