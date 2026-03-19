package com.gym.gymapp

interface Platform {
    val name: String
    val baseUrl: String
    fun currentTimeMillis(): Long
    fun exitApp()
}

expect fun getPlatform(): Platform