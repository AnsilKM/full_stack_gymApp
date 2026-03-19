package com.gym.gymapp.ui.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.Image

actual fun toImageBitmap(byteArray: ByteArray): ImageBitmap {
    return Image.makeFromEncoded(byteArray).toComposeImageBitmap()
}

actual fun ImageBitmap.toByteArray(): ByteArray {
    return org.jetbrains.skia.Image.makeFromBitmap(this.asSkiaBitmap()).encodeToData()?.bytes ?: byteArrayOf()
}
