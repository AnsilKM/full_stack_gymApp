package com.gym.gymapp

import android.app.Application
import com.gym.gymapp.di.initKoin
import org.koin.android.ext.koin.androidContext

class GymApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@GymApplication)
        }
    }
}
