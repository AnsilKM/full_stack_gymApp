package com.gym.gymapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.gym.gymapp.ui.components.NotificationBanner
import com.gym.gymapp.ui.navigation.Screens
import com.gym.gymapp.ui.theme.GymAppTheme
import com.gym.gymapp.network.NetworkClient
import com.gym.gymapp.ui.components.NotificationManager
import com.gym.gymapp.ui.components.AppNotificationType
import kotlinx.coroutines.flow.collectLatest

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import okio.FileSystem
import okio.Path.Companion.toPath

import coil3.disk.DiskCache
import coil3.disk.directory
import okio.Path.Companion.toPath

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        val platform = getPlatform()
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .diskCache {
                platform.cacheDir?.let {
                    DiskCache.Builder()
                        .directory(it.toPath().resolve("image_cache"))
                        .maxSizeBytes(1024L * 1024 * 100) // 100MB
                        .build()
                }
            }
            .logger(coil3.util.DebugLogger())
            .build()
    }

    GymAppTheme {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            Navigator(Screens.Splash()) { navigator ->

                // Global Listener for Authentication Events (e.g. Session Expiry)
                LaunchedEffect(Unit) {
                    NetworkClient.authEvents.collectLatest { event ->
                        when(event) {
                            is NetworkClient.AuthEvent.SessionExpired -> {
                                println("APP_LOG: Handling global Session Expired event...")
                                // 1. Clear local state
                                NetworkClient.authToken = null
                                NetworkClient.currentUser = null
                                
                                // 2. Show user-friendly message
                                NotificationManager.showNotification(
                                    "Session expired. Please login again.", 
                                    AppNotificationType.ERROR
                                )
                                
                                // 3. Redirect to log in and clear stack
                                navigator.replaceAll(Screens.Login())
                            }
                        }
                    }
                }

                SlideTransition(navigator)
            }
            
            NotificationBanner()
        }
    }
}