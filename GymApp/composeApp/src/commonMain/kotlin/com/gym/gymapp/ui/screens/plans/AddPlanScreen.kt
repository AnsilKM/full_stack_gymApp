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
import androidx.compose.ui.unit.sp
import com.gym.gymapp.ui.components.AppNotificationType
import com.gym.gymapp.ui.components.NotificationManager
import com.gym.gymapp.ui.components.AppLoader
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
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isTablet = maxWidth > 600.dp
        val formWidth = if (isTablet) 500.dp else maxWidth

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = if (isTablet) (maxWidth - formWidth) / 2 else 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.newPlanName,
                onValueChange = { viewModel.onNewPlanNameChange(it) },
                label = { Text("Plan Name *") },
                placeholder = { Text("e.g. Monthly Intro") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Words
                )
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

            Text("Plan Duration", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            
            // Quick duration chips - Responsive Row
            val durations = listOf(
                "1" to "1 Month",
                "3" to "3 Months",
                "6" to "6 Months",
                "12" to "1 Year"
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                durations.forEach { (value, label) ->
                    val isSelected = uiState.newPlanDuration == value
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.onNewPlanDurationChange(value) },
                        label = { Text(label, maxLines = 1, fontSize = if (isTablet) 12.sp else 10.sp) }, 
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            OutlinedTextField(
                value = uiState.newPlanDuration,
                onValueChange = { 
                    if (it.all { char -> char.isDigit() }) {
                        viewModel.onNewPlanDurationChange(it) 
                    }
                },
                label = { Text("Custom Duration (Months)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = { Text("Months", modifier = Modifier.padding(end = 12.dp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)) },
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

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.savePlan() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    AppLoader()
                } else {
                    Text(if (uiState.isEditMode) "Update Plan" else "Save & Publish Plan", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    }

}
