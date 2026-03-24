package com.gym.gymapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.data.models.MembershipPlan
import com.gym.gymapp.data.repository.PlanRepository
import com.gym.gymapp.data.repository.SessionManager
import com.gym.gymapp.data.repository.GymRepository
import kotlinx.coroutines.launch

data class PlansUiState(
    val plans: List<MembershipPlan> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val editingPlanId: String? = null,
    val addSuccess: Boolean = false,
    
    // New/Edit Plan Form
    val newPlanName: String = "",
    val newPlanDescription: String = "",
    val newPlanPrice: String = "",
    val newPlanDuration: String = "1"
) {
    val isEditMode: Boolean get() = editingPlanId != null
}


class PlansViewModel(
    private val repository: PlanRepository,
    private val gymRepository: GymRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    var uiState by mutableStateOf(PlansUiState())
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

    fun loadPlans() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val gymId = ensureGymId()
            if (gymId != null) {
                try {
                    val result = repository.getPlans(gymId)
                    uiState = uiState.copy(plans = result, isLoading = false)
                } catch (e: Exception) {
                    uiState = uiState.copy(isLoading = false, errorMessage = "Failed to load plans: ${e.message}")
                }
            } else {
                uiState = uiState.copy(isLoading = false, errorMessage = "Gym ID not found")
            }
        }
    }

    fun onNewPlanNameChange(value: String) { uiState = uiState.copy(newPlanName = value) }
    fun onNewPlanDescriptionChange(value: String) { uiState = uiState.copy(newPlanDescription = value) }
    fun onNewPlanPriceChange(value: String) { uiState = uiState.copy(newPlanPrice = value) }
    fun onNewPlanDurationChange(value: String) { uiState = uiState.copy(newPlanDuration = value) }

    fun initEdit(plan: MembershipPlan) {
        uiState = uiState.copy(
            editingPlanId = plan.id,
            newPlanName = plan.name,
            newPlanDescription = plan.description ?: "",
            newPlanPrice = plan.price.toInt().toString(),
            newPlanDuration = plan.durationMonths.toString(),
            errorMessage = null
        )
    }

    fun savePlan() {
        val name = uiState.newPlanName.trim()
        val price = uiState.newPlanPrice.toDoubleOrNull()
        val duration = uiState.newPlanDuration.toIntOrNull()
        val isEditMode = uiState.editingPlanId != null
        
        if (name.isBlank()) {
            uiState = uiState.copy(errorMessage = "Plan name is required")
            return
        }
        if (price == null || price <= 0) {
            uiState = uiState.copy(errorMessage = "Please enter a valid price")
            return
        }
        if (duration == null || duration <= 0) {
            uiState = uiState.copy(errorMessage = "Please enter a valid duration in months")
            return
        }


        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val gymId = ensureGymId()
            if (gymId != null) {
                val planId = if (isEditMode) {
                    uiState.editingPlanId!!
                } else {
                    // Generate slug from name for brand new plans
                    name.lowercase().trim()
                        .replace(Regex("[^a-z0-9]"), "-")
                        .replace(Regex("-+"), "-")
                        .trim('-')
                }

                val plan = MembershipPlan(
                    id = planId,
                    name = name,
                    description = uiState.newPlanDescription,
                    price = price,
                    durationMonths = duration,
                    gymId = gymId
                )
                try {
                    val success = if (isEditMode) {
                        repository.updatePlan(uiState.editingPlanId!!, plan)
                    } else {
                        repository.createPlan(plan)
                    }
                    
                    if (success) {
                        uiState = uiState.copy(
                            isLoading = false, 
                            addSuccess = true,
                            newPlanName = "",
                            newPlanDescription = "",
                            newPlanPrice = "",
                            newPlanDuration = "1"
                        )
                        loadPlans()
                    } else {
                        uiState = uiState.copy(isLoading = false, errorMessage = if (isEditMode) "Failed to update" else "Failed to create")
                    }
                } catch (e: Exception) {
                    uiState = uiState.copy(isLoading = false, errorMessage = "Error: ${e.message}")
                }
            }
        }
    }

    fun deletePlan(planId: String) {
        if (planId.isBlank()) return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val success = repository.deletePlan(planId)
                if (success) {
                    uiState = uiState.copy(isLoading = false)
                    loadPlans()
                } else {
                    uiState = uiState.copy(isLoading = false, errorMessage = "Failed to delete plan")
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = "Error: ${e.message}")
            }
        }
    }


    fun resetAddSuccess() { 
        uiState = uiState.copy(
            addSuccess = false,
            editingPlanId = null 
        ) 
    }
    fun clearError() { uiState = uiState.copy(errorMessage = null) }
}
