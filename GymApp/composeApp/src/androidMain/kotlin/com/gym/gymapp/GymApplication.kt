package com.gym.gymapp

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.gym.gymapp.di.initKoin
import org.koin.android.ext.koin.androidContext

class GymApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@GymApplication)
        }
    }
}
