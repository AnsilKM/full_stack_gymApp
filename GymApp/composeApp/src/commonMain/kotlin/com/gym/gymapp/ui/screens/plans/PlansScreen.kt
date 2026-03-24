package com.gym.gymapp.ui.screens.plans

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.gym.gymapp.ui.viewmodels.PlansViewModel
import com.gym.gymapp.ui.components.*
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun PlansScreen(
    onAddPlan: () -> Unit,
    onEditPlan: (com.gym.gymapp.data.models.MembershipPlan) -> Unit,
    viewModel: PlansViewModel = koinViewModel()
) {

    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadPlans()
    }

    var planToDelete by remember { mutableStateOf<com.gym.gymapp.data.models.MembershipPlan?>(null) }

    if (planToDelete != null) {
        ActionConfirmationDialog(
            onDismissRequest = { planToDelete = null },
            onConfirm = { planToDelete?.id?.let { viewModel.deletePlan(it) } },
            title = "Delete Plan?",
            message = "Are you sure you want to delete the '${planToDelete?.name}' plan? This may affect members currently on this plan.",
            confirmText = "Delete",
            confirmColor = Color(0xFFEF4444),
            icon = Icons.Default.DeleteForever,
            iconColor = Color(0xFFEF4444)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Membership Plans",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            )
        }


        if (uiState.isLoading && uiState.plans.isEmpty()) {
            AppLoader(isFullPage = true)
        } else if (uiState.plans.isEmpty()) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("No plans added yet", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        } else {
            uiState.plans.forEach { plan ->
                PlanCard(
                    name = plan.name,
                    price = "₹${plan.price.toInt()}",
                    duration = "${plan.durationMonths} Months",
                    features = plan.description?.split(",") ?: listOf("Gym Access"),
                    color = MaterialTheme.colorScheme.surface,
                    onEdit = { 
                        onEditPlan(plan)
                    },
                    onDelete = {
                        planToDelete = plan
                    }
                )
                Spacer(Modifier.height(16.dp))
            }
        }

    }
}


@Composable
fun PlanCard(name: String, price: String, duration: String, features: List<String>, color: Color, onEdit: () -> Unit, onDelete: () -> Unit, isPopular: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(duration, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                
                Text(price, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(12.dp))

            features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                    Icon(
                        Icons.Default.CheckCircle, 
                        contentDescription = null, 
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), 
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        feature.trim(), 
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onEdit,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                
                Spacer(Modifier.width(8.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f), shape = RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
