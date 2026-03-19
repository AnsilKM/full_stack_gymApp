package com.gym.gymapp.ui.screens.members

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gym.gymapp.ui.viewmodels.MemberDetailsViewModel
import com.gym.gymapp.ui.components.ProfileImage
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailsScreen(
    memberId: String,
    onBack: () -> Unit,
    viewModel: MemberDetailsViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    LaunchedEffect(memberId) {
        viewModel.loadMember(memberId)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            com.gym.gymapp.ui.components.NotificationManager.showNotification(
                it,
                com.gym.gymapp.ui.components.AppNotificationType.ERROR
            )
        }
    }

    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            com.gym.gymapp.ui.components.NotificationManager.showNotification(
                "Member deleted",
                com.gym.gymapp.ui.components.AppNotificationType.SUCCESS
            )
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Member Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleEdit() }) {
                        Icon(
                            if (uiState.isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = "Edit"
                        )
                    }
                    IconButton(onClick = { viewModel.deleteMember() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.member != null) {
            val member = uiState.member
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!uiState.isEditing) {
                    // Digital Membership Card
                    MembershipCard(
                        name = member.name,
                        memberId = member.id,
                        status = member.status,
                        plan = member.gym?.name ?: "Professional Member",
                        imageUrl = member.photoUrl
                    )

                    Spacer(Modifier.height(24.dp))
                }

                if (uiState.isEditing) {
                    EditMemberForm(viewModel)
                } else {
                    MemberInfoDisplay(member.name, member.email, member.photoUrl, member.phone, member.bloodGroup, member.status, member.createdAt)
                }

                Spacer(Modifier.height(24.dp))

                // Status Toggle
                Button(
                    onClick = { viewModel.toggleStatus() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.member.status == "ACTIVE") Color.Red.copy(alpha = 0.1f) else Color.Green.copy(
                            alpha = 0.1f
                        ),
                        contentColor = if (uiState.member.status == "ACTIVE") Color.Red else Color.Green
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(if (uiState.member.status == "ACTIVE") "Deactivate Member" else "Activate Member")
                }
            }
        }
    }

}


@Composable
fun MembershipCard(name: String, memberId: String, status: String, plan: String, imageUrl: String?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E293B),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(Modifier.align(Alignment.TopStart)) {
                ProfileImage(
                    imageUrl = imageUrl,
                    name = name,
                    size = 48
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "GYM PASS",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    name.uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    plan,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // QR Code Section
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(100.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                QRCodeGenerator(data = memberId)
            }

            // Status Badge
            Surface(
                modifier = Modifier.align(Alignment.TopEnd),
                color = if (status == "ACTIVE") Color(0xFF10B981).copy(alpha = 0.2f) else Color.Red.copy(
                    alpha = 0.2f
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    status,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = if (status == "ACTIVE") Color(0xFF10B981) else Color.Red,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                "ID: ${memberId.take(8).uppercase()}",
                modifier = Modifier.align(Alignment.BottomStart),
                color = Color.White.copy(alpha = 0.4f),
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun QRCodeGenerator(data: String) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val size = 21 // QR version 1 size
        val cellSize = this.size.width / size

        // Seed based on data
        val seed = data.hashCode()

        for (i in 0 until size) {
            for (j in 0 until size) {
                // Draw finding patterns (corners)
                val isFindingPattern =
                    (i < 7 && j < 7) || (i < 7 && j >= size - 7) || (i >= size - 7 && j < 7)

                if (isFindingPattern) {
                    val isBorder = i == 0 || i == 6 || j == 0 || j == 6 ||
                            (i == size - 7 && (j == 0 || j == 6)) ||
                            (i == size - 1 && (j == 0 || j == 6)) ||
                            (j == size - 7 && (i == 0 || i == 6)) ||
                            (j == size - 1 && (i == 0 || i == 6))

                    val isInner = (i in 2..4 && j in 2..4) ||
                            (i in 2..4 && j in (size - 5)..(size - 3)) ||
                            (i in (size - 5)..(size - 3) && j in 2..4)

                    // Draw the corner patterns
                    // In a real QR code these are fixed, here we simplify
                    val rowInPattern = if (i >= size - 7) i - (size - 7) else i
                    val colInPattern = if (j >= size - 7) j - (size - 7) else j

                    val shouldFill =
                        (rowInPattern == 0 || rowInPattern == 6 || colInPattern == 0 || colInPattern == 6) ||
                                (rowInPattern in 2..4 && colInPattern in 2..4)

                    if (shouldFill) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(j * cellSize, i * cellSize),
                            size = Size(cellSize + 0.5f, cellSize + 0.5f)
                        )
                    }
                } else {
                    // Draw random data dots based on hash
                    val cellHash = abs((seed xor (i * 1337 + j * 997))) % 100
                    if (cellHash < 45) { // ~45% density
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(j * cellSize, i * cellSize),
                            size = Size(cellSize + 0.5f, cellSize + 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MemberInfoDisplay(name: String, email: String?, imageUrl: String?, phone: String?, bloodGroup: String?, status: String, createdAt: String) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        ProfileImage(
            imageUrl = imageUrl,
            name = name,
            size = 100
        )
        Spacer(Modifier.height(16.dp))
        Text(
            name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            email ?: "N/A",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        InfoCard("Full Name", name, Icons.Default.Person)
        InfoCard("Email Address", email ?: "N/A", Icons.Default.Email)
        InfoCard("Phone Number", phone ?: "N/A", Icons.Default.Phone)
        InfoCard("Blood Group", bloodGroup ?: "Not Specified", Icons.Default.Favorite)
        InfoCard("Account Status", status, Icons.Default.Info)
        InfoCard("Joined Date", createdAt.split("T")[0], Icons.Default.DateRange)
    }
}

@Composable
fun InfoCard(label: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.3f
            )
        )
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EditMemberForm(viewModel: MemberDetailsViewModel) {
    val uiState = viewModel.uiState
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = uiState.editName,
            onValueChange = { viewModel.onEditNameChange(it) },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        OutlinedTextField(
            value = uiState.editEmail,
            onValueChange = { viewModel.onEditEmailChange(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        OutlinedTextField(
            value = uiState.editPhone,
            onValueChange = { viewModel.onEditPhoneChange(it) },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )
        OutlinedTextField(
            value = uiState.editBloodGroup,
            onValueChange = { viewModel.onEditBloodGroupChange(it) },
            label = { Text("Blood Group") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        )

        Button(
            onClick = { viewModel.updateMember() },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Update Details")
        }
    }
}
