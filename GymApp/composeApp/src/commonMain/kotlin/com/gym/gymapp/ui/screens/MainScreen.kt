package com.gym.gymapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.gym.gymapp.ui.components.GymBottomBar
import com.gym.gymapp.ui.components.MainTab
import com.gym.gymapp.ui.navigation.Screens
import com.gym.gymapp.ui.screens.attendance.AttendanceScreen
import com.gym.gymapp.ui.screens.dashboard.DashboardScreen
import com.gym.gymapp.ui.screens.members.MemberListScreen
import com.gym.gymapp.ui.screens.profile.ProfileScreen
import com.gym.gymapp.ui.screens.payments.PaymentsScreen
import com.gym.gymapp.ui.screens.plans.PlansScreen
import com.gym.gymapp.ui.screens.announcements.BroadcastScreen


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import com.gym.gymapp.ui.theme.Black

import com.gym.gymapp.ui.theme.Black
import com.gym.gymapp.AppBackHandler
import com.gym.gymapp.ui.components.NotificationManager
import com.gym.gymapp.ui.components.AppNotificationType
import kotlinx.coroutines.delay

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainScreen : Screen {
    @Composable
    override fun Content() {
        var selectedTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        var lastBackPressTime by remember { mutableStateOf(0L) }

        AppBackHandler(enabled = true) {
            if (navigator.canPop) {
                navigator.pop()
            } else if (selectedTab != MainTab.HOME) {
                selectedTab = MainTab.HOME
            } else {
                val currentTime = com.gym.gymapp.getPlatform().currentTimeMillis()
                if (currentTime - lastBackPressTime < 2000) {
                    com.gym.gymapp.getPlatform().exitApp()
                } else {
                    lastBackPressTime = currentTime
                    NotificationManager.showNotification("Press again to exit", AppNotificationType.INFO)
                }
            }
        }

        Scaffold(
            bottomBar = {
                GymBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            },
            floatingActionButton = {
                if (selectedTab == MainTab.MEMBERS || selectedTab == MainTab.PLANS) {
                    FloatingActionButton(
                        onClick = { 
                            if (selectedTab == MainTab.MEMBERS) navigator.push(Screens.AddMember())
                            else navigator.push(Screens.AddPlan())
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Item")
                    }
                }
            },

            containerColor = MaterialTheme.colorScheme.background // Restore theme support
        ) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                when (selectedTab) {
                    MainTab.HOME -> DashboardScreen(
                        onAddMember = { navigator.push(Screens.AddMember()) },
                        onViewMembers = { selectedTab = MainTab.MEMBERS },
                        onViewAttendance = { selectedTab = MainTab.PAYMENTS }, // Linked to Payments for now
                        onViewProfile = { selectedTab = MainTab.PROFILE },
                        onMemberClick = { id -> navigator.push(Screens.MemberDetails(id)) }
                    )
                    MainTab.MEMBERS -> MemberListScreen(
                        onBack = { selectedTab = MainTab.HOME },
                        onMemberClick = { id -> navigator.push(Screens.MemberDetails(id)) },
                        onAddMember = { navigator.push(Screens.AddMember()) }
                    )
                    MainTab.PAYMENTS -> {
                        PaymentsScreen()
                    }
                    MainTab.PLANS -> {
                        PlansScreen(
                            onAddPlan = { navigator.push(Screens.AddPlan()) },
                            onEditPlan = { navigator.push(Screens.AddPlan()) }
                        )
                    }


                    MainTab.PROFILE -> ProfileScreen(
                        onBack = { selectedTab = MainTab.HOME },
                        onLogout = { 
                            navigator.replaceAll(Screens.Login())
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun PlaceholderScreen(title: String, description: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title, 
            style = MaterialTheme.typography.headlineMedium, 
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = description, 
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
