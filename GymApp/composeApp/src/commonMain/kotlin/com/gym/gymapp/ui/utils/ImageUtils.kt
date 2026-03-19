package com.gym.gymapp.ui.utils

import androidx.compose.ui.graphics.ImageBitmap

expect fun toImageBitmap(byteArray: ByteArray): ImageBitmap
expect fun ImageBitmap.toByteArray(): ByteArray
