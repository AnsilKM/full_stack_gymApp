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
            viewModel.onPickedImageChange(bitmap.toByteArray())
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
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = viewModel.uiState.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = {
                    Text("Email Address")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
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
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedTextField(
                value = viewModel.uiState.bloodGroup,
                onValueChange = { viewModel.onBloodGroupChange(it) },
                label = { Text("Blood Group (O+, AB-, etc.)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )


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
                            text = { Text("${plan.name} - $${plan.price}") },
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
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Register Member", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }

        // Image Preview Dialog
        if (showPreview && viewModel.uiState.pickedImage != null) {
            androidx.compose.ui.window.Dialog(
                onDismissRequest = { showPreview = false }
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f).padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Black
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        androidx.compose.foundation.Image(
                            bitmap = com.gym.gymapp.ui.utils.toImageBitmap(viewModel.uiState.pickedImage!!),
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )

                        Row(
                            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    showPreview = false
                                    openImagePicker = true
                                },
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = Color.White
                                )
                            }
                            IconButton(
                                onClick = {
                                    showPreview = false
                                    viewModel.onPickedImageChange(null)
                                },
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = Color.White
                                )
                            }
                            IconButton(onClick = { showPreview = false }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
