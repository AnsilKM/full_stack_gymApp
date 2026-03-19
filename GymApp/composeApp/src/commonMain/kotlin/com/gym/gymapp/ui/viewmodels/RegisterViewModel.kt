package com.gym.gymapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.data.models.GymRegisterRequest
import com.gym.gymapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class RegisterUIState(
    val gymName: String = "",
    val ownerName: String = "",
    val email: String = "",
    val password: String = "",
    val phone: String = "",
    val address: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val success: Boolean = false
)

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {
    var uiState by mutableStateOf(RegisterUIState())
        private set

    fun onGymNameChange(value: String) { uiState = uiState.copy(gymName = value) }
    fun onOwnerNameChange(value: String) { uiState = uiState.copy(ownerName = value) }
    fun onEmailChange(value: String) { uiState = uiState.copy(email = value) }
    fun onPasswordChange(value: String) { uiState = uiState.copy(password = value) }
    fun onPhoneChange(value: String) { uiState = uiState.copy(phone = value) }
    fun onAddressChange(value: String) { uiState = uiState.copy(address = value) }

    fun register() {
        val state = uiState
        if (state.gymName.isBlank() || state.ownerName.isBlank() || state.email.isBlank() || state.password.isBlank()) {
            uiState = uiState.copy(errorMessage = "Please fill all mandatory fields")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val request = GymRegisterRequest(
                gymName = state.gymName,
                ownerName = state.ownerName,
                email = state.email,
                password = state.password,
                phone = state.phone,
                address = state.address
            )
            val result = repository.register(request)
            result.onSuccess {
                uiState = uiState.copy(isLoading = false, success = true)
            }
            result.onFailure {
                uiState = uiState.copy(isLoading = false, errorMessage = it.message ?: "Registration failed")
            }
        }
    }

    fun clearError() { uiState = uiState.copy(errorMessage = null) }
    fun resetState() { uiState = RegisterUIState() }
}
