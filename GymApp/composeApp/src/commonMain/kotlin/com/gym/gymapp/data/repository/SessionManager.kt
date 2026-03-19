package com.gym.gymapp.data.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import com.gym.gymapp.data.models.UserDto
import kotlinx.serialization.json.Json

class SessionManager {
    private val settings = Settings()
    private val json = Json { ignoreUnknownKeys = true }

    var authToken: String?
        get() = settings["auth_token"]
        set(value) {
            if (value != null) settings["auth_token"] = value
            else settings.remove("auth_token")
        }

    var userData: UserDto?
        get() {
            val userJson: String? = settings["user_data"]
            return if (userJson != null) {
                try {
                    json.decodeFromString<UserDto>(userJson)
                } catch (e: Exception) {
                    null
                }
            } else null
        }
        set(value) {
            if (value != null) {
                val userJson = json.encodeToString(UserDto.serializer(), value)
                settings["user_data"] = userJson
            } else {
                settings.remove("user_data")
            }
        }

    var gymId: String?
        get() = settings["gym_id"]
        set(value) {
            if (value != null) settings["gym_id"] = value
            else settings.remove("gym_id")
        }

    fun clear() {

        settings.clear()
    }

    fun isLoggedIn(): Boolean {
        return authToken != null
    }
}
