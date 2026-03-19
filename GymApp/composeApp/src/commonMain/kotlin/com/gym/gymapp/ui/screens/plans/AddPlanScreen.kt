package com.gym.gymapp.ui.screens.plans

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gym.gymapp.ui.components.AppNotificationType
import com.gym.gymapp.ui.components.NotificationManager
import com.gym.gymapp.ui.viewmodels.PlansViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlanScreen(
    plan: com.gym.gymapp.data.models.MembershipPlan? = null,
    onBack: () -> Unit,
    viewModel: PlansViewModel = koinViewModel()
) {

    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    LaunchedEffect(plan) {
        plan?.let { viewModel.initEdit(it) }
    }

    LaunchedEffect(uiState.addSuccess) {

        if (uiState.addSuccess) {
            val msg = if (uiState.isEditMode) "Plan updated successfully!" else "Plan created successfully!"
            NotificationManager.showNotification(msg, AppNotificationType.SUCCESS)
            viewModel.resetAddSuccess()
            onBack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            NotificationManager.showNotification(it, AppNotificationType.ERROR)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Edit Plan" else "Create New Plan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.newPlanName,
                onValueChange = { viewModel.onNewPlanNameChange(it) },
                label = { Text("Plan Name *") },
                placeholder = { Text("e.g. Monthly Intro") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = uiState.newPlanPrice,
                onValueChange = { viewModel.onNewPlanPriceChange(it) },
                label = { Text("Price (₹) *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )

            OutlinedTextField(
                value = uiState.newPlanDuration,
                onValueChange = { viewModel.onNewPlanDurationChange(it) },
                label = { Text("Duration (Months) *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                )
            )


            OutlinedTextField(
                value = uiState.newPlanDescription,
                onValueChange = { viewModel.onNewPlanDescriptionChange(it) },
                label = { Text("Features (comma separated)") },
                placeholder = { Text("Locker, Cardio, Personal Training") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { viewModel.savePlan() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(if (uiState.isEditMode) "Update Plan" else "Save & Publish Plan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

}
