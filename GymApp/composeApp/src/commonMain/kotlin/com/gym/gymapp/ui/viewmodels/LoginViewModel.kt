package com.gym.gymapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.data.models.LoginRequest
import com.gym.gymapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class LoginUIState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    var uiState by mutableStateOf(LoginUIState())
        private set

    private val _loginEvents = MutableSharedFlow<LoginEvent>()
    val loginEvents: SharedFlow<LoginEvent> = _loginEvents.asSharedFlow()

    sealed class LoginEvent {
        object Success : LoginEvent()
    }

    fun onEmailChange(newValue: String) { uiState = uiState.copy(email = newValue) }
    fun onPasswordChange(newValue: String) { uiState = uiState.copy(password = newValue) }

    fun login() {
        if (uiState.email.isEmpty() || uiState.password.isEmpty()) {
            uiState = uiState.copy(errorMessage = "Please fill in all fields")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)
        println("APP_LOG: Attempting login for email: ${uiState.email}")

        viewModelScope.launch {
            val result = repository.login(LoginRequest(uiState.email, uiState.password))
            result.onSuccess {
                println("APP_LOG: Login successful")
                uiState = uiState.copy(isLoading = false)
                _loginEvents.emit(LoginEvent.Success)
            }
            result.onFailure {
                println("APP_LOG: Login failed - ${it.message}")
                uiState = uiState.copy(errorMessage = it.message ?: "Login failed", isLoading = false)
            }
        }
    }

    fun resetState() {
        uiState = LoginUIState()
    }
}
