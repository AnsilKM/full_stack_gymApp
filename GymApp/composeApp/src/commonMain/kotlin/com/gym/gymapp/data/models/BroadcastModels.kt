package com.gym.gymapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class BroadcastRequest(
    val gymId: String,
    val message: String
)

@Serializable
data class BroadcastResponse(
    val status: String,
    val sentCount: Int = 0,
    val skippedCount: Int = 0
)

@Serializable
data class BroadcastLog(
    val id: String,
    val title: String,
    val message: String,
    val createdAt: String
)
