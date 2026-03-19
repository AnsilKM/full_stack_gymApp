package com.gym.gymapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.data.models.Member
import com.gym.gymapp.data.repository.MemberRepository
import kotlinx.coroutines.launch

data class MemberListUIState(
    val members: List<Member> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class MemberListViewModel(private val repository: MemberRepository) :
    ViewModel() {
    var uiState by mutableStateOf(MemberListUIState())
        private set

    val filteredMembers: List<Member>
        get() = if (uiState.searchQuery.isEmpty()) {
            uiState.members
        } else {
            uiState.members.filter {
                it.name.contains(
                    uiState.searchQuery,
                    ignoreCase = true
                )
            }
        }

    init {
        loadMembers()
    }

    fun onSearchQueryChange(query: String) {
        uiState = uiState.copy(searchQuery = query)
    }

    fun loadMembers() {
        println("APP_LOG: Fetching member list")
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            val result = repository.getMembers()
            result.onSuccess {
                println("APP_LOG: Successfully loaded ${it.size} members")
                uiState = uiState.copy(members = it, isLoading = false)
            }
            result.onFailure {
                println("APP_LOG: Failed to load members: ${it.message}")
                uiState = uiState.copy(isLoading = false)
            }
        }
    }

    fun resetState() {
        uiState = uiState.copy(searchQuery = "")
    }
}
