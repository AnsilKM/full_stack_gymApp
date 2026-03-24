package com.gym.gymapp.ui.screens.announcements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gym.gymapp.ui.components.AppNotificationType
import com.gym.gymapp.ui.components.AppLoader
import com.gym.gymapp.ui.components.NotificationManager
import com.gym.gymapp.ui.viewmodels.BroadcastViewModel
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BroadcastScreen(onBack: () -> Unit, viewModel: BroadcastViewModel = koinViewModel()) {
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.loadLogs()
    }

    LaunchedEffect(uiState.sendSuccess) {
        if (uiState.sendSuccess) {
            NotificationManager.showNotification("Broadcast sent to all members!", AppNotificationType.SUCCESS)
            viewModel.resetSuccess()
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
                title = { Text("WhatsApp Broadcast", fontSize = 16.sp, fontWeight = FontWeight.Black) },
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
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(padding)
                .padding(16.dp)
        ) {
            // Composer
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("New Broadcast", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Send a message to all active members via WhatsApp.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.message,
                        onValueChange = { viewModel.onMessageChange(it) },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        placeholder = { Text("Type your announcement here...", fontSize = 14.sp) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.sendBroadcast() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isSending && uiState.message.isNotBlank()
                    ) {
                        if (uiState.isSending) {
                            AppLoader()
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Send to All Members", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                    }
                }
            }

            Text("Previous Broadcasts", fontWeight = FontWeight.Black, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))

            if (uiState.isLoading && uiState.logs.isEmpty()) {
                AppLoader(isFullPage = true)
            } else if (uiState.logs.isEmpty()) {
                Text("No past broadcasts found.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            } else {
                uiState.logs.forEach { log ->
                    BroadcastLogCard(
                        title = log.title,
                        message = log.message,
                        date = log.createdAt.split("T")[0]
                    )
                }
            }
        }
    }
}

@Composable
fun BroadcastLogCard(title: String, message: String, date: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Campaign, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp), contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(date, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
            Spacer(Modifier.height(8.dp))
            Text(message, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
        }
    }
}
