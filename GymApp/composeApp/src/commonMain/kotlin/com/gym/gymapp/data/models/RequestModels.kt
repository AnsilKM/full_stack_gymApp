package com.gym.gymapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CreateMemberRequest(
    val name: String,
    val email: String? = null,
    val phone: String,
    val gymId: String,
    val planId: String? = null,
    val bloodGroup: String? = null,
    val photoUrl: String? = null,
    val status: String = "ACTIVE"
)

@Serializable
data class AttendanceCheckInRequest(
    val memberId: String,
    val gymId: String
)

@Serializable
data class UpdateMemberRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val bloodGroup: String? = null,
    val photoUrl: String? = null,
    val status: String? = null,
    val planId: String? = null
)
