package com.gym.gymapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.data.models.Attendance
import com.gym.gymapp.data.repository.GymRepository
import kotlinx.coroutines.launch

data class AttendanceUIState(
    val attendanceList: List<Attendance> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val gymName: String = ""
)

class AttendanceViewModel(private val repository: GymRepository) : ViewModel() {
    var uiState by mutableStateOf(AttendanceUIState())
        private set

    init {
        loadAttendance()
    }

    fun loadAttendance() {
        println("APP_LOG: Fetching attendance data")
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                // For now, we take the first gym found
                val gymsResult = repository.getGyms()
                gymsResult.onSuccess { gyms ->
                    if (gyms.isNotEmpty()) {
                        val gym = gyms[0]
                        val attendanceResult = repository.getTodayAttendance(gym.id)
                        attendanceResult.onSuccess { list ->
                            println("APP_LOG: Successfully loaded ${list.size} attendance records for ${gym.name}")
                            uiState = uiState.copy(
                                attendanceList = list,
                                gymName = gym.name,
                                isLoading = false
                            )
                        }
                        attendanceResult.onFailure {
                            println("APP_LOG: Failed to load attendance: ${it.message}")
                            uiState = uiState.copy(errorMessage = "Failed to load attendance", isLoading = false)
                        }
                    } else {
                        println("APP_LOG: No gyms found for user")
                        uiState = uiState.copy(errorMessage = "No gyms found", isLoading = false)
                    }
                }
                gymsResult.onFailure {
                    uiState = uiState.copy(errorMessage = "Failed to load gyms", isLoading = false)
                    println("APP_LOG: Failed to load gyms: ${it.message}")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = e.message, isLoading = false)
                println("APP_LOG: Exception loading attendance: ${e.message}")
            }
        }
    }

    fun resetState() {
        uiState = AttendanceUIState()
    }
}
