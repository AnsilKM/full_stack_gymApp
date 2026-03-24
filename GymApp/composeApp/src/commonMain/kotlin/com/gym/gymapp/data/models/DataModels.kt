package com.gym.gymapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val status: Boolean,
    val message: String,
    val data: T
)

@Serializable
data class Gym(
    val id: String,
    val name: String,
    val address: String,
    val phone: String? = null,
    val ownerId: String
)

@Serializable
data class Member(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val gymId: String,
    val status: String,
    val bloodGroup: String? = null,
    val photoUrl: String? = null,
    val gym: Gym? = null,
    val joinDate: String? = null,
    val expiryDate: String? = null,
    val createdAt: String,
    val isExpired: Boolean? = null,
    val joiningDateDisplay: String? = null,
    val expiryDateDisplay: String? = null
)

@Serializable

data class MembershipPlan(
    val id: String,
    val name: String,
    val description: String? = null,
    val price: Double,
    val durationMonths: Int,
    val gymId: String
)

@Serializable
data class Attendance(
    val id: String,
    val memberId: String,
    val gymId: String,
    val date: String,
    val checkInTime: String,
    val member: Member? = null
)

@Serializable
data class Payment(
    val id: String,
    val memberId: String,
    val gymId: String,
    val amount: Double,
    val date: String,
    val method: String,
    val planId: String
)

@Serializable
data class GymRegisterRequest(
    val gymName: String,
    val ownerName: String,
    val email: String,
    val password: String,
    val address: String? = null,
    val phone: String? = null
)

