package com.gym.gymapp.ui.utils

import androidx.compose.runtime.Composable

@Composable
actual fun AppBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No physical back button on iOS
}
