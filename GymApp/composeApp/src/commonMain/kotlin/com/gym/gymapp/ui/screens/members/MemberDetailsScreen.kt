package com.gym.gymapp.ui.screens.members

import androidx.compose.foundation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.gym.gymapp.ui.utils.AppBackHandler
import com.gym.gymapp.getPlatform
import com.gym.gymapp.ui.components.*
import com.gym.gymapp.ui.utils.*
import com.gym.gymapp.ui.viewmodels.MemberDetailsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel
import network.chaintech.cmpimagepickncrop.CMPImagePickNCropDialog
import network.chaintech.cmpimagepickncrop.imagecropper.rememberImageCropper
import kotlinx.datetime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberDetailsScreen(
    memberId: String,
    onBack: () -> Unit,
    viewModel: MemberDetailsViewModel = koinViewModel()
) {
    val uiState = viewModel.uiState
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val imageCropper = rememberImageCropper()
    
    var openImagePicker by remember { mutableStateOf(false) }
    var showFullScreenPreview by remember { mutableStateOf(false) }

    // Logic for loading and side effects
    LaunchedEffect(memberId) { viewModel.loadMember(memberId) }
    
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { NotificationManager.showNotification(it, AppNotificationType.ERROR) }
    }
    
    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) {
            NotificationManager.showNotification("Member deleted", AppNotificationType.SUCCESS)
            onBack()
        }
    }

    CMPImagePickNCropDialog(
        imageCropper = imageCropper,
        openImagePicker = openImagePicker,
        imagePickerDialogHandler = { openImagePicker = it },
        showCameraOption = true,
        showGalleryOption = true,
        cropEnable = true,
        selectedImageCallback = { bitmap ->
            scope.launch(Dispatchers.Default) {
                val bytes = bitmap.toByteArray(75)
                withContext(Dispatchers.Main) { viewModel.onPickedImageChange(bytes) }
            }
        },
        selectedImageFileCallback = { }
    )

    // Backpress handler for Edit mode
    AppBackHandler(enabled = uiState.isEditing) {
        viewModel.toggleEdit()
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            snackbarHostState.showSnackbar("Status updated successfully")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit Member" else "Member Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (uiState.isEditing) viewModel.toggleEdit() 
                        else onBack() 
                    }) {
                        Icon(
                            if (uiState.isEditing) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack, 
                            if (uiState.isEditing) "Close" else "Back"
                        )
                    }
                },
                actions = {
                    if (!uiState.isEditing && uiState.member != null) {
                        IconButton(onClick = { viewModel.toggleEdit() }) {
                            Icon(Icons.Default.Edit, "Edit")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.member == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(strokeWidth = 3.dp)
            }
        } else if (uiState.member != null) {
            val member = uiState.member
            
            if (uiState.isEditing) {
                // Full Screen Edit Form overlay
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
                ) {
                    EditMemberForm(viewModel, onPickImage = { openImagePicker = true }, onShowPreview = { showFullScreenPreview = true })
                }
            } else {
                // Profile Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                ) {
                    val daysRemaining = remember(member.expiryDate) {
                        try {
                            if (member.expiryDate == null) 0
                            else {
                                val expiry = Instant.parse(member.expiryDate).toLocalDateTime(TimeZone.currentSystemDefault()).date
                                val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
                                today.daysUntil(expiry)
                            }
                        } catch (e: Exception) { 0 }
                    }
                    
                    val isActuallyExpired = member.isExpired == true || (member.status == "ACTIVE" && member.expiryDate != null && daysRemaining < 0)
                    val isNearExpiry = !isActuallyExpired && member.status == "ACTIVE" && daysRemaining in 0..5

                    // 1. Simple Header
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                    Surface(
                        modifier = Modifier.size(100.dp).clickable { showFullScreenPreview = true },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        ProfileImage(imageUrl = member.photoUrl, name = member.name, size = 100)
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Text(member.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(member.gym?.name ?: "Member", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    
                    Spacer(Modifier.height(12.dp))

                    val pillColor = when {
                        member.status != "ACTIVE" -> Color.Gray
                        isActuallyExpired -> Color.Red
                        isNearExpiry -> Color(0xFFF97316)
                        else -> Color(0xFF4CAF50)
                    }
                    val pillText = when {
                        member.status != "ACTIVE" -> "DEACTIVATED"
                        isActuallyExpired -> "EXPIRED"
                        isNearExpiry -> "ENDING SOON"
                        else -> "ACTIVE"
                    }

                    Surface(
                        color = pillColor.copy(0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = pillText,
                            color = pillColor,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 2. Quick Actions
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isActuallyExpired || isNearExpiry) {
                        Button(
                            onClick = { viewModel.startRenewing() },
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isActuallyExpired) Color.Red else Color(0xFFF97316)
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Renew", fontSize = 13.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { member.phone?.let { getPlatform().openUrl("tel:$it") } },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.Default.Call, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Call", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { 
                            member.phone?.let {
                                val number = it.replace("+", "").replace(" ", "")
                                getPlatform().openUrl("https://wa.me/$number")
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Chat, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Chat", fontSize = 13.sp)
                    }
                }

                    Spacer(Modifier.height(24.dp))

                    // 3. Information Sections
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        
                        val progress = remember(member.joinDate, member.expiryDate) {
                            try {
                                if (member.joinDate == null || member.expiryDate == null) 0f
                                else {
                                    val join = Instant.parse(member.joinDate).toEpochMilliseconds()
                                    val expiry = Instant.parse(member.expiryDate).toEpochMilliseconds()
                                    val now = Clock.System.now().toEpochMilliseconds()
                                    val total = (expiry - join).toFloat()
                                    if (total <= 0) 0f
                                    else {
                                        val remaining = (expiry - now).toFloat()
                                        (remaining / total).coerceIn(0f, 1f)
                                    }
                                }
                            } catch (e: Exception) { 0f }
                        }

                        // Membership Details
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("Membership", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(8.dp))
                                        IconButton(onClick = { viewModel.toggleEdit() }, modifier = Modifier.size(24.dp)) {
                                            Icon(Icons.Default.Edit, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                        }
                                    }
                                    if (member.status == "ACTIVE" && daysRemaining > 0) {
                                        Surface(
                                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = "$daysRemaining days left", 
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                color = MaterialTheme.colorScheme.primary, 
                                                style = MaterialTheme.typography.labelMedium, 
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                
                                Spacer(Modifier.height(20.dp))
                                
                                if (member.status == "ACTIVE" && daysRemaining > 0) {
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier.fillMaxWidth().height(8.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        strokeCap = StrokeCap.Round,
                                    )
                                    Spacer(Modifier.height(24.dp))
                                } else if (member.status == "ACTIVE" && daysRemaining <= 0) {
                                    Surface(
                                        color = Color.Red.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "MEMBERSHIP EXPIRED", 
                                            modifier = Modifier.padding(12.dp),
                                            color = Color.Red, 
                                            style = MaterialTheme.typography.labelLarge, 
                                            fontWeight = FontWeight.Black,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                    Spacer(Modifier.height(24.dp))
                                }

                                Row(Modifier.fillMaxWidth()) {
                                    InfoColumn(label = "Join Date", value = member.joiningDateDisplay ?: "N/A", modifier = Modifier.weight(1f))
                                    InfoColumn(label = "Expiry Date", value = member.expiryDateDisplay ?: "N/A", modifier = Modifier.weight(1f))
                                }
                            }
                        }

                        // Personal Info
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("Personal Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                ProfileDetailRow(Icons.Default.Email, member.email ?: "Not provided")
                                ProfileDetailRow(Icons.Default.Phone, member.phone ?: "Not provided")
                                ProfileDetailRow(Icons.Default.Favorite, member.bloodGroup ?: "Not set")
                            }
                        }

                        // Digital ID
                        MembershipCard(
                            name = member.name,
                            memberId = member.id,
                            status = member.status,
                            plan = member.gym?.name ?: "Member",
                            imageUrl = member.photoUrl,
                            onShowPreview = { showFullScreenPreview = true }
                        )

                        Spacer(Modifier.height(24.dp))

                        // Danger Zone
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                        
                        var showDeactivateDialog by remember { mutableStateOf(false) }
                        TextButton(
                            onClick = { showDeactivateDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text(if (member.status == "ACTIVE") "Deactivate Member" else "Reactivate Member")
                        }

                        if (showDeactivateDialog) {
                            ActionConfirmationDialog(
                                onDismissRequest = { showDeactivateDialog = false },
                                onConfirm = { 
                                    viewModel.toggleStatus() 
                                    showDeactivateDialog = false
                                },
                                title = if (member.status == "ACTIVE") "Deactivate Member?" else "Reactivate Member?",
                                message = if (member.status == "ACTIVE") 
                                    "Are you sure you want to deactivate ${member.name}? Access will be restricted." 
                                    else "Reactivating will restore member access.",
                                confirmText = if (member.status == "ACTIVE") "Deactivate" else "Reactivate",
                                confirmColor = if (member.status == "ACTIVE") Color.Red else Color(0xFF4CAF50),
                                icon = if (member.status == "ACTIVE") Icons.Default.PersonOff else Icons.Default.PersonAdd,
                                iconColor = if (member.status == "ACTIVE") Color.Red else Color(0xFF4CAF50)
                            )
                        }

                        var showDeleteDialog by remember { mutableStateOf(false) }
                        TextButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                        ) {
                            Text("Delete Member Profile")
                        }

                        if (showDeleteDialog) {
                            ActionConfirmationDialog(
                                onDismissRequest = { showDeleteDialog = false },
                                onConfirm = { viewModel.deleteMember() },
                                title = "Delete Member?",
                                message = "Are you sure you want to delete ${member.name} permanently?",
                                confirmText = "Delete",
                                confirmColor = Color.Red,
                                icon = Icons.Default.Warning,
                                iconColor = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }

    if (uiState.isRenewing) {
        // Renewal is now handled inside EditMemberForm per user request
    }

    if (showFullScreenPreview && uiState.member != null) {
        FullScreenImagePreview(
            imageUrl = uiState.member.photoUrl,
            onClose = { showFullScreenPreview = false }
        )
    }
}

@Composable
fun InfoColumn(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun ProfileDetailRow(icon: ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary.copy(0.7f))
        Spacer(Modifier.width(12.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun EditMemberForm(viewModel: MemberDetailsViewModel, onPickImage: () -> Unit, onShowPreview: () -> Unit) {
    val uiState = viewModel.uiState
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(if (uiState.isRenewing) "Membership Renewal" else "Edit Member Details", fontWeight = FontWeight.Black, fontSize = 20.sp)
            
            // Image Edit Section
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
                    .clickable { 
                        if (uiState.pickedImage != null || !uiState.member?.photoUrl.isNullOrEmpty()) {
                            onShowPreview()
                        } else {
                            onPickImage()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                if (uiState.pickedImage != null) {
                    Image(
                        bitmap = toImageBitmap(uiState.pickedImage),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else if (!uiState.member?.photoUrl.isNullOrEmpty()) {
                    ProfileImage(
                        imageUrl = uiState.member.photoUrl,
                        name = uiState.member.name,
                        size = 100
                    )
                } else {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp).size(16.dp)
                    )
                }
            }

            Text(
                "Tap photo to change",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.editName,
                onValueChange = { viewModel.onEditNameChange(it) },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = uiState.editEmail,
                onValueChange = { viewModel.onEditEmailChange(it) },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = uiState.editPhone,
                onValueChange = { viewModel.onEditPhoneChange(it) },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Blood Group selection
            var showBloodGroupMenu by remember { mutableStateOf(false) }
            val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
            
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.editBloodGroup,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Blood Group") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(onClick = { showBloodGroupMenu = !showBloodGroupMenu }) {
                            Icon(if (showBloodGroupMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
                        }
                    }
                )
                Box(modifier = Modifier.matchParentSize().clickable { showBloodGroupMenu = true })
                
                DropdownMenu(
                    expanded = showBloodGroupMenu,
                    onDismissRequest = { showBloodGroupMenu = false }
                ) {
                    bloodGroups.forEach { bg ->
                        DropdownMenuItem(
                            text = { Text(bg) },
                            onClick = {
                                viewModel.onEditBloodGroupChange(bg)
                                showBloodGroupMenu = false
                            }
                        )
                    }
                }
            }

            if (uiState.isRenewing) {
                // Plan selection (Only visible during Renew)
                var showPlanMenu by remember { mutableStateOf(false) }
                val selectedPlan = uiState.availablePlans.find { it.id == uiState.editPlanId }
                
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedPlan?.let { "${it.name} (${it.durationMonths} Months)" } ?: "Select New Plan",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Membership Plan") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = { showPlanMenu = !showPlanMenu }) {
                                Icon(if (showPlanMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
                            }
                        }
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { showPlanMenu = true })
                    
                    DropdownMenu(
                        expanded = showPlanMenu,
                        onDismissRequest = { showPlanMenu = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        uiState.availablePlans.forEach { plan ->
                            DropdownMenuItem(
                                text = { 
                                    Column {
                                        Text(plan.name, fontWeight = FontWeight.Bold)
                                        Text("${plan.durationMonths} Months • ${plan.price}", style = MaterialTheme.typography.labelSmall)
                                    }
                                },
                                onClick = {
                                    viewModel.onEditPlanChange(plan.id)
                                    showPlanMenu = false
                                }
                            )
                        }
                    }
                }
            }

            // Save Buttons
            Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { viewModel.toggleEdit() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = { viewModel.updateMember() },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                    else Text("Save Changes")
                }
            }
        }
    }
}

@Composable
fun MembershipCard(
    name: String, 
    memberId: String, 
    status: String, 
    plan: String, 
    imageUrl: String?,
    onShowPreview: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                )
                .padding(20.dp)
        ) {
            Column(Modifier.align(Alignment.TopStart)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(48.dp).clickable { onShowPreview() },
                        shape = CircleShape,
                        color = Color.Transparent
                    ) {
                        ProfileImage(imageUrl = imageUrl, name = name, size = 48)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(name.uppercase(), color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                        Text(plan, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(Modifier.height(12.dp))
                Text("DIGITAL PASS", color = Color.White.copy(alpha = 0.3f), style = MaterialTheme.typography.labelSmall, letterSpacing = 2.sp)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(80.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                QRCodeGenerator(data = memberId)
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
        val size = 21 
        val cellSize = this.size.width / size
        val seed = data.hashCode()

        for (i in 0 until size) {
            for (j in 0 until size) {
                val isFindingPattern = (i < 7 && j < 7) || (i < 7 && j >= size - 7) || (i >= size - 7 && j < 7)
                if (isFindingPattern) {
                    val rowInPattern = if (i >= size - 7) i - (size - 7) else i
                    val colInPattern = if (j >= size - 7) j - (size - 7) else j
                    val shouldFill = (rowInPattern == 0 || rowInPattern == 6 || colInPattern == 0 || colInPattern == 6) || (rowInPattern in 2..4 && colInPattern in 2..4)
                    if (shouldFill) {
                        drawRect(color = Color.Black, topLeft = androidx.compose.ui.geometry.Offset(j * cellSize, i * cellSize), size = androidx.compose.ui.geometry.Size(cellSize + 0.5f, cellSize + 0.5f))
                    }
                } else {
                    val cellHash = kotlin.math.abs((seed xor (i * 1337 + j * 997))) % 100
                    if (cellHash < 45) {
                        drawRect(color = Color.Black, topLeft = androidx.compose.ui.geometry.Offset(j * cellSize, i * cellSize), size = androidx.compose.ui.geometry.Size(cellSize + 0.5f, cellSize + 0.5f))
                    }
                }
            }
        }
    }
}
