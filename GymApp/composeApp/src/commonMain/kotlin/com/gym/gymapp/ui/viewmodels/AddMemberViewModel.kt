package com.gym.gymapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.data.models.CreateMemberRequest
import com.gym.gymapp.data.models.MembershipPlan
import com.gym.gymapp.data.repository.MemberRepository
import com.gym.gymapp.data.repository.GymRepository
import kotlinx.coroutines.launch

data class AddMemberUIState(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val bloodGroup: String = "",
    val pickedImage: ByteArray? = null,
    val selectedPlanId: String? = null,
    val availablePlans: List<MembershipPlan> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AddMemberUIState
        if (name != other.name) return false
        if (email != other.email) return false
        if (phone != other.phone) return false
        if (bloodGroup != other.bloodGroup) return false
        if (!pickedImage.contentEquals(other.pickedImage)) return false
        if (selectedPlanId != other.selectedPlanId) return false
        if (availablePlans != other.availablePlans) return false
        if (isLoading != other.isLoading) return false
        if (errorMessage != other.errorMessage) return false
        if (saveSuccess != other.saveSuccess) return false
        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + bloodGroup.hashCode()
        result = 31 * result + (pickedImage?.contentHashCode() ?: 0)
        result = 31 * result + (selectedPlanId?.hashCode() ?: 0)
        result = 31 * result + availablePlans.hashCode()
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + saveSuccess.hashCode()
        return result
    }
}

class AddMemberViewModel(
    private val memberRepository: MemberRepository,
    private val gymRepository: GymRepository
) : ViewModel() {
    var uiState by mutableStateOf(AddMemberUIState())
        private set

    init {
        loadPlans()
    }

    private fun loadPlans() {
        println("APP_LOG: Loading plans for AddMember screen")
        viewModelScope.launch {
            try {
                val gymsResult = gymRepository.getGyms()
                gymsResult.onSuccess { gyms ->
                    val gymId = gyms.firstOrNull()?.id
                    if (gymId != null) {
                        println("APP_LOG: Fetching plans for gym: $gymId")
                        val plansResult = gymRepository.getMembershipPlans(gymId)
                        plansResult.onSuccess { plans ->
                            println("APP_LOG: Successfully loaded ${plans.size} plans")
                            uiState = uiState.copy(availablePlans = plans, selectedPlanId = plans.firstOrNull()?.id)
                        }
                        plansResult.onFailure {
                            println("APP_LOG: Failed to load plans - ${it.message}")
                        }
                    } else {
                        println("APP_LOG: No gym found to load plans")
                    }
                }
                gymsResult.onFailure {
                    println("APP_LOG: Failed to load gyms for plans - ${it.message}")
                }
            } catch (e: Exception) {
                println("APP_LOG: Exception in loadPlans - ${e.message}")
            }
        }
    }

    fun onNameChange(newValue: String) { uiState = uiState.copy(name = newValue) }
    fun onEmailChange(newValue: String) { uiState = uiState.copy(email = newValue) }
    fun onPhoneChange(newValue: String) { uiState = uiState.copy(phone = newValue) }
    fun onBloodGroupChange(newValue: String) { uiState = uiState.copy(bloodGroup = newValue) }
    fun onPickedImageChange(newValue: ByteArray?) { uiState = uiState.copy(pickedImage = newValue) }
    fun onPlanChange(newValue: String) { uiState = uiState.copy(selectedPlanId = newValue) }

    fun saveMember() {
        if (uiState.name.isEmpty() || uiState.phone.isEmpty() || uiState.selectedPlanId == null) {
            uiState = uiState.copy(errorMessage = "Name, Phone, and Plan are required")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val gymsResult = gymRepository.getGyms()
                val gymId = gymsResult.getOrNull()?.firstOrNull()?.id ?: "default_gym_id"

                var finalPhotoUrl: String? = null
                
                // 1. Upload image if available
                uiState.pickedImage?.let { bytes ->
                    val uploadResult = memberRepository.uploadImage(bytes)
                    uploadResult.onSuccess { url ->
                        finalPhotoUrl = url
                    }.onFailure {
                        uiState = uiState.copy(errorMessage = "Image upload failed: ${it.message}", isLoading = false)
                        return@launch
                    }
                }

                // 2. Create member with the uploaded photo URL
                val request = CreateMemberRequest(
                    name = uiState.name,
                    email = uiState.email.ifEmpty { null },
                    phone = uiState.phone,
                    gymId = gymId,
                    planId = uiState.selectedPlanId,
                    bloodGroup = uiState.bloodGroup.ifEmpty { null },
                    photoUrl = finalPhotoUrl,
                    status = "ACTIVE"
                )

                val result = memberRepository.createMember(request)
                result.onSuccess {
                    uiState = uiState.copy(saveSuccess = true, isLoading = false)
                }
                result.onFailure {
                    uiState = uiState.copy(errorMessage = it.message ?: "Failed to save member", isLoading = false)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Error: ${e.message}", isLoading = false)
            }
        }
    }

    fun clearFields() {
        uiState = AddMemberUIState(availablePlans = uiState.availablePlans, selectedPlanId = uiState.availablePlans.firstOrNull()?.id)
    }
}
