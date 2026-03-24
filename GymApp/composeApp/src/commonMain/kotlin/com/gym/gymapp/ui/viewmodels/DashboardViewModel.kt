package com.gym.gymapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.data.models.Attendance
import com.gym.gymapp.data.models.Gym
import com.gym.gymapp.data.repository.GymRepository
import com.gym.gymapp.data.models.Member
import com.gym.gymapp.data.repository.MemberRepository
import kotlinx.coroutines.launch

data class DashboardUIState(
    val totalMembers: String = "0",
    val todayAttendance: String = "0",
    val totalRevenue: String = "₹0",
    val monthlyGrowth: String = "0%",
    val recentActivity: List<Attendance> = emptyList(),
    val recentMembers: List<Member> = emptyList(),
    val isLoading: Boolean = true,
    val gymName: String = ""
)


class DashboardViewModel(
    private val repository: GymRepository,
    private val memberRepository: MemberRepository
) : ViewModel() {
    var uiState by mutableStateOf(DashboardUIState())
        private set

    init {
        loadDashboardData()
    }

    private var isFirstLoad = true

    fun loadDashboardData() {
        println("APP_LOG: Fetching dashboard data")
        if (isFirstLoad) {
            uiState = uiState.copy(isLoading = true)
        }
        viewModelScope.launch {
            val gymsResult = repository.getGyms()
            gymsResult.onSuccess { gyms ->
                if (gyms.isNotEmpty()) {
                    isFirstLoad = false
                    val gym = gyms[0]
                    
                    val statsResult = repository.getDashboardStats(gym.id)
                    val attendanceResult = repository.getTodayAttendance(gym.id)
                    
                    var totalMembers = "0"
                    var count = "0"
                    var revenue = "₹0"
                    var growth = "0%"
                    var activity = emptyList<Attendance>()
                    var recentMembers = emptyList<Member>()
                    
                    statsResult.onSuccess { stats ->
                        totalMembers = stats.activeMembers.toString()
                        revenue = "₹${stats.totalRevenue.toInt()}"
                        growth = if (stats.monthlyGrowth >= 0) "+${stats.monthlyGrowth}%" else "${stats.monthlyGrowth}%"
                        count = stats.todayCheckins.toString()
                    }
                    
                    val membersResult = memberRepository.getMembers(gym.id)
                    membersResult.onSuccess { members ->
                        recentMembers = members.take(5)
                    }
                    
                    attendanceResult.onSuccess { attendance ->
                        activity = attendance.take(5)
                    }
                    
                    uiState = uiState.copy(
                        gymName = gym.name,
                        totalMembers = totalMembers,
                        todayAttendance = count,
                        totalRevenue = revenue,
                        monthlyGrowth = growth,
                        recentActivity = activity,
                        recentMembers = recentMembers
                    )

                    println("APP_LOG: Successfully loaded dashboard for gym: ${gym.name}")
                } else {
                    println("APP_LOG: No gyms found for dashboard stats")
                }
            }
            gymsResult.onFailure {
                println("APP_LOG: Failed to load gyms for dashboard - ${it.message}")
            }
            uiState = uiState.copy(isLoading = false)
        }
    }
}
