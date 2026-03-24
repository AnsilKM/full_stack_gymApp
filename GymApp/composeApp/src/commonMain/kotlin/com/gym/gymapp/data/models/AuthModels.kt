package com.gym.gymapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val access_token: String,
    val user: UserDto? = null
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String? = null,
    val phone: String? = null,
    val role: String? = null,
    val gyms: List<Gym>? = null,
    val branchGymId: String? = null
)

