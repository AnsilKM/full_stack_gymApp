package com.gym.gymapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class DashboardStats(
    val activeMembers: Int = 0,
    val totalRevenue: Double = 0.0,
    val monthlyGrowth: Double = 0.0,
    val todayCheckins: Int = 0
)

@Serializable
data class ErrorResponse(
    val message: String,
    val statusCode: Int? = null
)
