package com.gym.gymapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gym.gymapp.ui.utils.AppBackHandler
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun FullScreenImagePreview(
    imageUrl: String? = null,
    imageBitmap: androidx.compose.ui.graphics.ImageBitmap? = null,
    onClose: () -> Unit
) {
    // Handle physical back button
    AppBackHandler { onClose() }

    val scope = rememberCoroutineScope()
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    
    // Animation for dismissing by swiping
    val swipeOffset = remember { Animatable(0f) }
    val backgroundAlpha by animateFloatAsState(
        targetValue = if (Math.abs(swipeOffset.value) > 200f) 0f else 1f,
        animationSpec = tween(300)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f * backgroundAlpha))
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale > 1f) 1f else 2.5f
                        if (scale == 1f) offset = androidx.compose.ui.geometry.Offset.Zero
                    },
                    onTap = { /* Just eat the tap to prevent clicks under it */ }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        if (scale == 1f && Math.abs(swipeOffset.value) > 300f) {
                            onClose()
                        } else {
                            scope.launch { swipeOffset.animateTo(0f) }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        if (scale == 1f) {
                            change.consume()
                            scope.launch { swipeOffset.snapTo(swipeOffset.value + dragAmount.y) }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                    // Only allow pan when zoomed in, or allow vertical swipe to dismiss
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    
                    if (scale > 1f) {
                        offset += pan
                    } else {
                        // Drag to dismiss logic could go here, but using draggable is better
                    }
                }
            }
    ) {
        val imageModifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(0, swipeOffset.value.roundToInt()) }
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offset.x,
                translationY = offset.y,
                alpha = backgroundAlpha
            )

        if (imageBitmap != null) {
            androidx.compose.foundation.Image(
                bitmap = imageBitmap,
                contentDescription = "Full Screen Preview",
                modifier = imageModifier,
                contentScale = ContentScale.Fit
            )
        } else {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Full Screen Preview",
                modifier = imageModifier,
                contentScale = ContentScale.Fit
            )
        }

        // Action Buttons Overlay
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }
    }
}
