package com.gym.gymapp.ui.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AppNotification(
    val message: String,
    val type: AppNotificationType
)

enum class AppNotificationType {
    SUCCESS, ERROR, INFO
}

object NotificationManager {
    private val scope = CoroutineScope(Dispatchers.Main)
    
    private val _notification = MutableStateFlow<AppNotification?>(null)
    val notification = _notification.asStateFlow()

    fun showNotification(message: String, type: AppNotificationType) {
        _notification.value = AppNotification(message, type)
        scope.launch {
            delay(3000)
            if (_notification.value?.message == message) {
                _notification.value = null
            }
        }
    }

    fun dismiss() {
        _notification.value = null
    }
}
