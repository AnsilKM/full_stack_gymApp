package com.gym.gymapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.data.models.Member
import com.gym.gymapp.data.repository.MemberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.datetime.*
import kotlinx.coroutines.*

data class MemberListUIState(
    val members: List<Member> = emptyList(),
    val filteredMembers: List<Member> = emptyList(),
    val searchQuery: String = "",
    val statusFilter: String = "ALL", // ALL, ACTIVE, INACTIVE, EXPIRED
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val page: Int = 1,
    val hasMore: Boolean = true
)

class MemberListViewModel(private val repository: MemberRepository) : ViewModel() {
    var uiState by mutableStateOf(MemberListUIState())
        private set

    private var filterJob: Job? = null

    init {
        loadMembers()
    }

    fun onSearchQueryChange(query: String) {
        uiState = uiState.copy(searchQuery = query)
        applyFilters()
    }

    fun onStatusFilterChange(status: String) {
        uiState = uiState.copy(statusFilter = status)
        applyFilters()
    }

    private fun applyFilters() {
        // Cancel any pending filter operations
        filterJob?.cancel()
        
        filterJob = viewModelScope.launch(Dispatchers.Default) {
            // Add a small debounce to search
            if (uiState.searchQuery.isNotEmpty()) {
                delay(100) 
            }

            val searchLower = uiState.searchQuery.lowercase()
            val statusFilter = uiState.statusFilter
            val allMembers = uiState.members
            val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

            val result = allMembers.filter { member ->
                val matchesSearch = if (searchLower.isEmpty()) true 
                                   else member.name.lowercase().contains(searchLower) || (member.email?.lowercase()?.contains(searchLower) ?: false)
                
                val actuallyExpired = member.isExpired ?: false

                val matchesStatus = when (statusFilter) {
                    "ALL" -> true
                    "EXPIRED" -> actuallyExpired
                    "ACTIVE" -> member.status == "ACTIVE" && !actuallyExpired
                    "INACTIVE" -> member.status == "INACTIVE"
                    else -> member.status == statusFilter
                }
                
                matchesSearch && matchesStatus
            }

            withContext(Dispatchers.Main) {
                uiState = uiState.copy(filteredMembers = result)
            }
        }
    }

    private var isFirstLoad = true
    private val limit = 50
    
    fun loadMembers(isNextPage: Boolean = false) {
        if (uiState.isLoading || (isNextPage && !uiState.hasMore)) return

        val targetPage = if (isNextPage) uiState.page + 1 else 1
        
        if (targetPage == 1) {
            uiState = uiState.copy(isRefreshing = true, isLoading = isFirstLoad)
        } else {
            uiState = uiState.copy(isLoading = true)
        }

        viewModelScope.launch {
            val result = repository.getMembers(page = targetPage, limit = limit)
            result.onSuccess {
                isFirstLoad = false
                val newMembers = if (targetPage == 1) it else uiState.members + it
                uiState = uiState.copy(
                    members = newMembers,
                    isLoading = false,
                    isRefreshing = false,
                    page = targetPage,
                    hasMore = it.size >= limit
                )
                applyFilters() // Trigger filter with new data
            }
            result.onFailure {
                uiState = uiState.copy(isLoading = false, isRefreshing = false)
            }
        }
    }

    fun loadNextPage() {
        if (uiState.searchQuery.isEmpty()) { // Only paginate when not searching for simplicity
            loadMembers(isNextPage = true)
        }
    }

    fun refresh() {
        loadMembers(isNextPage = false)
    }

    fun resetState() {
        uiState = uiState.copy(searchQuery = "")
        applyFilters()
    }
}
