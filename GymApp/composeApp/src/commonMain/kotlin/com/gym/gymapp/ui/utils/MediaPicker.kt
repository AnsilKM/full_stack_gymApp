package com.gym.gymapp.ui.utils

import androidx.compose.runtime.Composable

@Composable
expect fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): () -> Unit

@Composable
expect fun rememberCameraLauncher(onImagePicked: (ByteArray?) -> Unit): () -> Unit

enum class PermissionType {
    CAMERA,
    GALLERY
}

@Composable
expect fun rememberPermissionHandler(
    permission: PermissionType,
    onResult: (Boolean) -> Unit
): () -> Unit
