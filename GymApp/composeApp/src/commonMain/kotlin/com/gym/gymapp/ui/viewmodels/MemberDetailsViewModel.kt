package com.gym.gymapp.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gym.gymapp.data.models.Member
import com.gym.gymapp.data.models.UpdateMemberRequest
import com.gym.gymapp.data.repository.MemberRepository
import kotlinx.coroutines.launch

data class MemberDetailsUIState(
    val member: Member? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val updateSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    // Fields for editing
    val editName: String = "",
    val editEmail: String = "",
    val editPhone: String = "",
    val editBloodGroup: String = "",
    val editStatus: String = ""
)

class MemberDetailsViewModel(
    private val memberRepository: MemberRepository
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
                    errorMessage = null
                )
            } else {
                uiState = uiState.copy(isLoading = false, errorMessage = "Member not found")
                println("APP_LOG: Member not found handling ID: $memberId")
            }
        }
    }

    fun toggleEdit() {
        uiState = uiState.copy(isEditing = !uiState.isEditing)
    }

    fun onEditNameChange(newValue: String) { uiState = uiState.copy(editName = newValue) }
    fun onEditEmailChange(newValue: String) { uiState = uiState.copy(editEmail = newValue) }
    fun onEditPhoneChange(newValue: String) { uiState = uiState.copy(editPhone = newValue) }
    fun onEditBloodGroupChange(newValue: String) { uiState = uiState.copy(editBloodGroup = newValue) }
    fun onEditStatusChange(newValue: String) { uiState = uiState.copy(editStatus = newValue) }

    fun updateMember() {
        val memberId = uiState.member?.id ?: return
        println("APP_LOG: Updating member details for ID: $memberId")
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            val request = UpdateMemberRequest(
                name = uiState.editName,
                email = uiState.editEmail,
                phone = uiState.editPhone,
                bloodGroup = uiState.editBloodGroup,
                status = uiState.editStatus
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
