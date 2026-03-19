package com.gym.gymapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.data.models.UserDto
import com.gym.gymapp.data.repository.AuthRepository
import com.gym.gymapp.network.NetworkClient
import kotlinx.coroutines.launch

data class ProfileUIState(
    val user: UserDto? = null,
    val isLoading: Boolean = false
)

class ProfileViewModel(private val authRepository: AuthRepository) : ViewModel() {
    var uiState by mutableStateOf(ProfileUIState())
        private set

    init {
        loadProfile()
    }

    fun loadProfile() {
        uiState = uiState.copy(user = NetworkClient.currentUser)
    }

    fun logout(onComplete: () -> Unit) {
        println("APP_LOG: Logging out user: ${NetworkClient.currentUser?.email}")
        viewModelScope.launch {
            try {
                authRepository.logout()
                println("APP_LOG: User logged out successfully via API")
            } catch (e: Exception) {
                println("APP_LOG: Error during logout: ${e.message}")
            } finally {
                onComplete()
            }
        }
    }

    fun resetState() {
        uiState = ProfileUIState()
    }
}
