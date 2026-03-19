package com.gym.gymapp.ui.navigation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.gym.gymapp.ui.screens.auth.LoginScreen
import com.gym.gymapp.ui.screens.auth.RegisterGymScreen

import com.gym.gymapp.ui.screens.dashboard.DashboardScreen
import com.gym.gymapp.ui.screens.members.AddMemberScreen
import com.gym.gymapp.ui.screens.members.MemberListScreen
import com.gym.gymapp.ui.screens.members.MemberDetailsScreen
import com.gym.gymapp.ui.screens.profile.ProfileScreen
import com.gym.gymapp.ui.screens.MainScreen
import com.gym.gymapp.ui.screens.splash.SplashScreen
import com.gym.gymapp.ui.screens.payments.PaymentsScreen
import com.gym.gymapp.ui.screens.plans.PlansScreen
import com.gym.gymapp.ui.screens.plans.AddPlanScreen
import com.gym.gymapp.ui.screens.announcements.BroadcastScreen

object Screens {
    
    class Splash : Screen {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            SplashScreen(
                onAnimationFinished = { isLoggedIn ->
                    if (isLoggedIn) {
                        navigator.replaceAll(Main())
                    } else {
                        navigator.replaceAll(Login())
                    }
                }
            )
        }
    }

    class Login : Screen {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            LoginScreen(
                onLoginSuccess = {
                    navigator.replaceAll(Main())
                },
                onRegisterClick = {
                    navigator.push(RegisterGym())
                }
            )

        }
    }

    class Main : Screen {
        @Composable
        override fun Content() {
            MainScreen().Content()
        }
    }

    class RegisterGym : Screen {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            RegisterGymScreen(
                onBack = { navigator.pop() },
                onSuccess = { 
                    navigator.replaceAll(Main())
                }
            )
        }
    }


    class Payments : Screen {
        @Composable
        override fun Content() {
            PaymentsScreen()
        }
    }

    class Plans : Screen {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            PlansScreen(
                onAddPlan = { navigator.push(AddPlan()) },
                onEditPlan = { plan -> navigator.push(AddPlan(plan)) }
            )

        }
    }


    class AddPlan(val plan: com.gym.gymapp.data.models.MembershipPlan? = null) : Screen {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            AddPlanScreen(
                plan = plan,
                onBack = { navigator.pop() }
            )
        }
    }


    class Broadcast : Screen {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            BroadcastScreen(onBack = { navigator.pop() })
        }
    }

    class MemberList : Screen {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            MemberListScreen(
                onBack = { navigator.pop() },
                onMemberClick = { id -> navigator.push(MemberDetails(id)) },
                onAddMember = { navigator.push(AddMember()) }
            )
        }
    }

    class AddMember : Screen {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            AddMemberScreen(
                onBack = { navigator.pop() },
                onSuccess = { 
                    navigator.pop()
                    // navigator.push(MemberList()) // Avoid pushing list on top of main
                }
            )
        }
    }

    class MemberDetails(private val memberId: String) : Screen {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            MemberDetailsScreen(
                memberId = memberId,
                onBack = { navigator.pop() }
            )
        }
    }

    class Profile : Screen {
        @Composable
        override fun Content() {
            val navigator = LocalNavigator.currentOrThrow
            ProfileScreen(
                onBack = { navigator.pop() },
                onLogout = { 
                    navigator.replaceAll(Login())
                }
            )
        }
    }
}
