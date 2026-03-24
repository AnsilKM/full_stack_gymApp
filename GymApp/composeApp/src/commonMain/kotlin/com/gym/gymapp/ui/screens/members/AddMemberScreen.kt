package com.gym.gymapp.ui.screens.members

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import kotlinx.coroutines.*
import org.koin.compose.viewmodel.koinViewModel
import com.gym.gymapp.ui.viewmodels.AddMemberViewModel
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import com.gym.gymapp.ui.components.NotificationManager
import com.gym.gymapp.ui.components.AppLoader
import com.gym.gymapp.ui.components.AppNotificationType
import com.gym.gymapp.ui.utils.toByteArray
import com.gym.gymapp.ui.utils.toImageBitmap
import network.chaintech.cmpimagepickncrop.CMPImagePickNCropDialog
import network.chaintech.cmpimagepickncrop.imagecropper.rememberImageCropper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AddMemberViewModel = koinViewModel()
) {
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearFields()
        }
    }

    var showPlanMenu by remember { mutableStateOf(false) }

    val imageCropper = rememberImageCropper()
    val scope = rememberCoroutineScope()
    var openImagePicker by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.uiState.errorMessage) {
        viewModel.uiState.errorMessage?.let {
            NotificationManager.showNotification(it, AppNotificationType.ERROR)
        }
    }

    if (viewModel.uiState.saveSuccess) {
        LaunchedEffect(Unit) {
            NotificationManager.showNotification(
                "Member saved successfully",
                AppNotificationType.SUCCESS
            )
            viewModel.clearFields()
            onSuccess()
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
                withContext(Dispatchers.Main) {
                    viewModel.onPickedImageChange(bytes)
                }
            }
        },
        selectedImageFileCallback = { }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Member", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Picture Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier.size(120.dp).clickable {
                            if (viewModel.uiState.pickedImage != null) {
                                showPreview = true
                            } else {
                                openImagePicker = true
                            }
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (viewModel.uiState.pickedImage != null) {
                                androidx.compose.foundation.Image(
                                    bitmap = com.gym.gymapp.ui.utils.toImageBitmap(viewModel.uiState.pickedImage!!),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.size(72.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .size(36.dp)
                            .offset(x = 4.dp, y = 4.dp)
                            .clickable { openImagePicker = true },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        tonalElevation = 4.dp,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Profile Picture",
                                modifier = Modifier.size(18.dp),
                                tint = Color.White
                            )
                        }
                    }
                }

            }

            Text(
                buildAnnotatedString {
                    append("Personal Information ")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("*")
                    }
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = viewModel.uiState.name,
                onValueChange = { viewModel.onNameChange(it) },
                label = {
                    Text(buildAnnotatedString {
                        append("Full Name ")
                        withStyle(style = SpanStyle(color = Color.Red)) { append("*") }
                    })
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.Words
                )
            )

            OutlinedTextField(
                value = viewModel.uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = {
                    Text("Email Address")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
                )
            )

            OutlinedTextField(
                value = viewModel.uiState.phone,
                onValueChange = { viewModel.onPhoneChange(it) },
                label = {
                    Text(buildAnnotatedString {
                        append("Phone Number ")
                        withStyle(style = SpanStyle(color = Color.Red)) { append("*") }
                    })
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                )
            )

            // Blood Group selection dropdown
            var showBloodGroupMenu by remember { mutableStateOf(false) }
            val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
            
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = viewModel.uiState.bloodGroup,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Blood Group") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = { showBloodGroupMenu = !showBloodGroupMenu }) {
                            Icon(
                                if (showBloodGroupMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    },
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showBloodGroupMenu = true }
                )

                DropdownMenu(
                    expanded = showBloodGroupMenu,
                    onDismissRequest = { showBloodGroupMenu = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    bloodGroups.forEach { bg ->
                        DropdownMenuItem(
                            text = { Text(bg) },
                            onClick = {
                                viewModel.onBloodGroupChange(bg)
                                showBloodGroupMenu = false
                            }
                        )
                    }
                }
            }


            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                buildAnnotatedString {
                    append("Membership Plan ")
                    withStyle(style = SpanStyle(color = Color.Red)) { append("*") }
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Plan selection dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                val selectedPlan =
                    viewModel.uiState.availablePlans.find { it.id == viewModel.uiState.selectedPlanId }
                OutlinedTextField(
                    value = selectedPlan?.name
                        ?: if (viewModel.uiState.availablePlans.isEmpty()) "No Plans Available" else "Select a Plan",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(buildAnnotatedString {
                            append("Active Plan ")
                            withStyle(style = SpanStyle(color = Color.Red)) { append("*") }
                        })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        IconButton(onClick = { showPlanMenu = !showPlanMenu }) {
                            Icon(
                                if (showPlanMenu) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    },
                )
                // Transparent layer to capture clicks since OutlinedTextField might consume them
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            if (viewModel.uiState.availablePlans.isNotEmpty()) showPlanMenu = true
                        }
                )

                DropdownMenu(
                    expanded = showPlanMenu,
                    onDismissRequest = { showPlanMenu = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    if (viewModel.uiState.availablePlans.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No plans found") },
                            onClick = { showPlanMenu = false }
                        )
                    }
                    viewModel.uiState.availablePlans.forEach { plan ->
                        DropdownMenuItem(
                            text = { Text("${plan.name} - ₹${plan.price.toInt()}") },
                            onClick = {
                                viewModel.onPlanChange(plan.id)
                                showPlanMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.saveMember() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !viewModel.uiState.isLoading,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (viewModel.uiState.isLoading) {
                    AppLoader()
                } else {
                    Text("Register Member", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }

        // Image Preview Full Screen
        if (showPreview && viewModel.uiState.pickedImage != null) {
            Box(Modifier.fillMaxSize()) {
                com.gym.gymapp.ui.components.FullScreenImagePreview(
                    imageBitmap = com.gym.gymapp.ui.utils.toImageBitmap(viewModel.uiState.pickedImage!!),
                    onClose = { showPreview = false }
                )
                
                // Add action buttons on top of preview
                Row(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FilledTonalButton(
                        onClick = {
                            showPreview = false
                            openImagePicker = true
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.White.copy(alpha = 0.2f), contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Edit")
                    }

                    FilledTonalButton(
                        onClick = {
                            showPreview = false
                            viewModel.onPickedImageChange(null)
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.Red.copy(alpha = 0.2f), contentColor = Color.Red)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Delete")
                    }
                }
            }
        }
    }
}
