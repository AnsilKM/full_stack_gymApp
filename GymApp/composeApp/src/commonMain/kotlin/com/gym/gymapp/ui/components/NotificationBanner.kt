package com.gym.gymapp.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun NotificationBanner() {
    val notificationState by NotificationManager.notification.collectAsState()

    AnimatedVisibility(
        visible = notificationState != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier.zIndex(9999f)
    ) {
        notificationState?.let { notification ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Surface(
                    elevation = 2.dp,
                    color = when(notification.type) {
                        AppNotificationType.SUCCESS -> Color(0xFFECFDF5)
                        AppNotificationType.ERROR -> Color(0xFFFEF2F2)
                        AppNotificationType.INFO -> Color(0xFFF0F9FF)
                    },
                    contentColor = when(notification.type) {
                        AppNotificationType.SUCCESS -> Color(0xFF12AA21)
                        AppNotificationType.ERROR -> Color(0xFF991B1B)
                        AppNotificationType.INFO -> Color(0xFF0369A1)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = notification.message,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { NotificationManager.dismiss() },
                            tint = when(notification.type) {
                                AppNotificationType.SUCCESS -> Color(0xFF065F46).copy(alpha = 0.6f)
                                AppNotificationType.ERROR -> Color(0xFF991B1B).copy(alpha = 0.6f)
                                AppNotificationType.INFO -> Color(0xFF0369A1).copy(alpha = 0.6f)
                            }
                        )
                    }
                }
            }
        }
    }
}
