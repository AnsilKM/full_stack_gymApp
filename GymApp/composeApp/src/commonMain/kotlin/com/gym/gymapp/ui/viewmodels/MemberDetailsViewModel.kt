package com.gym.gymapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.data.models.Member
import com.gym.gymapp.data.models.UpdateMemberRequest
import com.gym.gymapp.data.models.MembershipPlan
import com.gym.gymapp.data.repository.MemberRepository
import com.gym.gymapp.data.repository.GymRepository
import kotlinx.coroutines.launch

data class MemberDetailsUIState(
    val member: Member? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val updateSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val isRenewing: Boolean = false,
    // Fields for editing
    val editName: String = "",
    val editEmail: String = "",
    val editPhone: String = "",
    val editBloodGroup: String = "",
    val editStatus: String = "",
    val editPlanId: String? = null,
    val availablePlans: List<MembershipPlan> = emptyList(),
    val pickedImage: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MemberDetailsUIState
        if (member != other.member) return false
        if (isEditing != other.isEditing) return false
        if (isLoading != other.isLoading) return false
        if (errorMessage != other.errorMessage) return false
        if (updateSuccess != other.updateSuccess) return false
        if (deleteSuccess != other.deleteSuccess) return false
        if (isRenewing != other.isRenewing) return false
        if (editName != other.editName) return false
        if (editEmail != other.editEmail) return false
        if (editPhone != other.editPhone) return false
        if (editBloodGroup != other.editBloodGroup) return false
        if (editStatus != other.editStatus) return false
        if (editPlanId != other.editPlanId) return false
        if (availablePlans != other.availablePlans) return false
        if (pickedImage != null) {
            if (other.pickedImage == null) return false
            if (!pickedImage.contentEquals(other.pickedImage)) return false
        } else if (other.pickedImage != null) return false
        return true
    }

    override fun hashCode(): Int {
        var result = member?.hashCode() ?: 0
        result = 31 * result + isEditing.hashCode()
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + updateSuccess.hashCode()
        result = 31 * result + deleteSuccess.hashCode()
        result = 31 * result + isRenewing.hashCode()
        result = 31 * result + editName.hashCode()
        result = 31 * result + editEmail.hashCode()
        result = 31 * result + editPhone.hashCode()
        result = 31 * result + editBloodGroup.hashCode()
        result = 31 * result + editStatus.hashCode()
        result = 31 * result + (editPlanId?.hashCode() ?: 0)
        result = 31 * result + availablePlans.hashCode()
        result = 31 * result + (pickedImage?.contentHashCode() ?: 0)
        return result
    }
}

class MemberDetailsViewModel(
    private val memberRepository: MemberRepository,
    private val gymRepository: GymRepository
) : ViewModel() {
    var uiState by mutableStateOf(MemberDetailsUIState())
        private set

    fun loadMember(memberId: String) {
        println("APP_LOG: Loading member details for ID: $memberId")
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            val members = memberRepository.getMembers().getOrNull()
            val member = members?.find { it.id == memberId }
            if (member != null) {
                uiState = uiState.copy(
                    member = member,
                    isLoading = false,
                    editName = member.name,
                    editEmail = member.email ?: "",
                    editPhone = member.phone ?: "",
                    editBloodGroup = member.bloodGroup ?: "",
                    editStatus = member.status,
                    editPlanId = null,
                    errorMessage = null
                )
                loadPlansForGym(member.gymId)
            } else {
                uiState = uiState.copy(isLoading = false, errorMessage = "Member not found")
                println("APP_LOG: Member not found handling ID: $memberId")
            }
        }
    }

    private fun loadPlansForGym(gymId: String) {
        viewModelScope.launch {
            val result = gymRepository.getMembershipPlans(gymId)
            result.onSuccess { plans ->
                uiState = uiState.copy(availablePlans = plans)
            }
        }
    }

    fun toggleEdit() {
        uiState = uiState.copy(isEditing = !uiState.isEditing, isRenewing = false)
    }

    fun startRenewing() {
        uiState = uiState.copy(isEditing = true, isRenewing = true, editStatus = "ACTIVE")
    }

    fun onEditNameChange(newValue: String) { uiState = uiState.copy(editName = newValue) }
    fun onEditEmailChange(newValue: String) { uiState = uiState.copy(editEmail = newValue) }
    fun onEditPhoneChange(newValue: String) {
        val digitsOnly = newValue.filter { it.isDigit() }
        if (digitsOnly.length <= 10) {
            uiState = uiState.copy(editPhone = digitsOnly)
        }
    }
    fun onEditBloodGroupChange(newValue: String) { uiState = uiState.copy(editBloodGroup = newValue) }
    fun onEditStatusChange(newValue: String) { uiState = uiState.copy(editStatus = newValue) }
    fun onEditPlanChange(newValue: String) { uiState = uiState.copy(editPlanId = newValue) }
    fun onPickedImageChange(newValue: ByteArray?) { uiState = uiState.copy(pickedImage = newValue) }

    fun updateMember() {
        val memberId = uiState.member?.id ?: return
        println("APP_LOG: Updating member details for ID: $memberId")
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            var updatedPhotoUrl: String? = uiState.member?.photoUrl

            // 1. Upload new image if picked
            uiState.pickedImage?.let { bytes ->
                val uploadResult = memberRepository.uploadImage(bytes)
                uploadResult.onSuccess { url ->
                    updatedPhotoUrl = url
                }.onFailure {
                    uiState = uiState.copy(isLoading = false, errorMessage = "Image upload failed: ${it.message}")
                    return@launch
                }
            }

            val request = UpdateMemberRequest(
                name = uiState.editName,
                email = uiState.editEmail,
                phone = uiState.editPhone,
                bloodGroup = uiState.editBloodGroup,
                status = uiState.editStatus,
                photoUrl = updatedPhotoUrl,
                planId = uiState.editPlanId
            )
            
            val result = memberRepository.updateMember(memberId, request)
            result.onSuccess { updatedMember ->
                println("APP_LOG: Successfully updated member: $memberId")
                uiState = uiState.copy(
                    member = updatedMember,
                    isEditing = false,
                    isLoading = false,
                    updateSuccess = true
                )
            }
            result.onFailure {
                println("APP_LOG: Failed to update member: ${it.message}")
                uiState = uiState.copy(isLoading = false, errorMessage = it.message ?: "Update failed")
            }
        }
    }

    fun deleteMember() {
        val memberId = uiState.member?.id ?: return
        println("APP_LOG: Deleting member ID: $memberId")
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            val result = memberRepository.deleteMember(memberId)
            result.onSuccess {
                uiState = uiState.copy(isLoading = false, deleteSuccess = true)
            }
            result.onFailure {
                uiState = uiState.copy(isLoading = false, errorMessage = it.message ?: "Delete failed")
            }
        }
    }
    
    fun toggleStatus() {
        val currentStatus = uiState.member?.status ?: return
        val newStatus = if (currentStatus == "ACTIVE") "INACTIVE" else "ACTIVE"
        uiState = uiState.copy(editStatus = newStatus)
        updateMember()
    }

    fun resetState() {
        uiState = MemberDetailsUIState()
    }
}
