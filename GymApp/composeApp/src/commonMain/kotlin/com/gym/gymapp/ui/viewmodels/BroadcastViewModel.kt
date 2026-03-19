package com.gym.gymapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.data.models.*
import com.gym.gymapp.data.repository.BroadcastRepository
import com.gym.gymapp.data.repository.SessionManager
import com.gym.gymapp.data.repository.GymRepository

import kotlinx.coroutines.launch

data class BroadcastUiState(
    val logs: List<BroadcastLog> = emptyList(),
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val sendSuccess: Boolean = false,
    val errorMessage: String? = null,
    val message: String = ""
)

class BroadcastViewModel(
    private val repository: BroadcastRepository,
    private val gymRepository: GymRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    var uiState by mutableStateOf(BroadcastUiState())
        private set

    private suspend fun ensureGymId(): String? {
        val cached = sessionManager.gymId
        if (cached != null) return cached
        
        return try {
            val gymsResult = gymRepository.getGyms()
            val gymId = gymsResult.getOrNull()?.firstOrNull()?.id
            if (gymId != null) {
                sessionManager.gymId = gymId
            }
            gymId
        } catch (e: Exception) {
            null
        }
    }

    fun onMessageChange(value: String) {
        uiState = uiState.copy(message = value)
    }

    fun loadLogs() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val gymId = ensureGymId()
            if (gymId != null) {
                try {
                    val result = repository.getLogs(gymId)
                    uiState = uiState.copy(logs = result, isLoading = false)
                } catch (e: Exception) {
                    uiState = uiState.copy(isLoading = false, errorMessage = "Failed to load logs: ${e.message}")
                }
            } else {
                uiState = uiState.copy(isLoading = false, errorMessage = "Gym ID not found")
            }
        }
    }

    fun sendBroadcast() {
        val currentMessage = uiState.message
        if (currentMessage.isBlank()) {
            uiState = uiState.copy(errorMessage = "Please enter a message")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSending = true, errorMessage = null)
            val gymId = ensureGymId()
            if (gymId != null) {
                try {
                    val response = repository.sendBroadcast(gymId, currentMessage)
                    if (response.status == "SUCCESS") {
                        uiState = uiState.copy(
                            isSending = false,
                            sendSuccess = true,
                            message = ""
                        )
                        loadLogs()
                    } else {
                        uiState = uiState.copy(isSending = false, errorMessage = "Broadcast failed: ${response.status}")
                    }
                } catch (e: Exception) {
                    uiState = uiState.copy(isSending = false, errorMessage = "Error: ${e.message}")
                }
            } else {
                uiState = uiState.copy(isSending = false, errorMessage = "Gym ID not found")
            }
        }
    }

    fun resetSuccess() {
        uiState = uiState.copy(sendSuccess = false)
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}
