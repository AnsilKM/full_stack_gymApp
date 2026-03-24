package com.gym.gymapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gym.gymapp.network.NetworkClient

import coil3.compose.AsyncImage

@Composable
fun ProfileImage(
    imageUrl: String?,
    name: String,
    modifier: Modifier = Modifier,
    size: Int = 56
) {
    val fullUrl = if (imageUrl != null && !imageUrl.startsWith("http")) {
        "${NetworkClient.BASE_URL}${if (imageUrl.startsWith("/")) "" else "/"}$imageUrl"
    } else {
        imageUrl
    }

    LaunchedEffect(fullUrl) {
        if (!fullUrl.isNullOrEmpty()) {
            println("APP_LOG: Loading profile image from: $fullUrl")
        }
    }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        if (!fullUrl.isNullOrEmpty()) {
            AsyncImage(
                model = fullUrl,
                contentDescription = name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = name.take(1).uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
