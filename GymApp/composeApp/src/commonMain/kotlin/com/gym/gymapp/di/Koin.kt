package com.gym.gymapp.di

import com.gym.gymapp.data.repository.SessionManager
import com.gym.gymapp.data.repository.AuthRepository
import com.gym.gymapp.data.repository.GymRepository
import com.gym.gymapp.data.repository.MemberRepository
import com.gym.gymapp.data.repository.PlanRepository
import com.gym.gymapp.data.repository.BroadcastRepository
import com.gym.gymapp.ui.viewmodels.AddMemberViewModel
import com.gym.gymapp.ui.viewmodels.AttendanceViewModel
import com.gym.gymapp.ui.viewmodels.DashboardViewModel
import com.gym.gymapp.ui.viewmodels.LoginViewModel
import com.gym.gymapp.ui.viewmodels.MemberListViewModel
import com.gym.gymapp.ui.viewmodels.MemberDetailsViewModel
import com.gym.gymapp.ui.viewmodels.RegisterViewModel
import com.gym.gymapp.ui.viewmodels.ProfileViewModel

import com.gym.gymapp.ui.viewmodels.PlansViewModel
import com.gym.gymapp.ui.viewmodels.BroadcastViewModel
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val appModule = module {
    // Session Persistent Store
    singleOf(::SessionManager)

    // Repositories
    singleOf(::AuthRepository)
    singleOf(::GymRepository)
    singleOf(::MemberRepository)
    singleOf(::PlanRepository)
    singleOf(::BroadcastRepository)

    // ViewModels
    viewModelOf(::LoginViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::MemberListViewModel)
    viewModelOf(::AddMemberViewModel)
    viewModelOf(::AttendanceViewModel)
    viewModelOf(::MemberDetailsViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::PlansViewModel)
    viewModelOf(::BroadcastViewModel)
    viewModelOf(::RegisterViewModel)
}



fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(appModule)
    }
