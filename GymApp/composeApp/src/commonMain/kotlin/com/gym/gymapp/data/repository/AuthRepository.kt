package com.gym.gymapp.data.repository

import com.gym.gymapp.data.models.LoginRequest
import com.gym.gymapp.data.models.LoginResponse
import com.gym.gymapp.network.ApiEndpoints
import com.gym.gymapp.network.NetworkClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

import com.gym.gymapp.data.repository.SessionManager

class AuthRepository(private val sessionManager: SessionManager) {
    private val client = NetworkClient.client
    
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = client.post(ApiEndpoints.LOGIN) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                val apiResponse: com.gym.gymapp.data.models.ApiResponse<LoginResponse> = response.body()
                val loginData = apiResponse.data
                
                // Persist session
                sessionManager.authToken = loginData.access_token
                sessionManager.userData = loginData.user
                
                // Set gymId from owned gyms or branchGymId
                val targetGymId = loginData.user?.gyms?.firstOrNull()?.id ?: loginData.user?.branchGymId
                sessionManager.gymId = targetGymId
                
                // Also update runtime client
                NetworkClient.authToken = loginData.access_token
                NetworkClient.currentUser = loginData.user
                NetworkClient.gymId = targetGymId

                
                Result.success(loginData)
            } else {
                Result.failure(Exception("Login failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: com.gym.gymapp.data.models.GymRegisterRequest): Result<LoginResponse> {
        return try {
            val response = client.post(ApiEndpoints.REGISTER) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                val apiResponse: com.gym.gymapp.data.models.ApiResponse<LoginResponse> = response.body()

                val loginData = apiResponse.data
                
                // Persist session
                sessionManager.authToken = loginData.access_token
                sessionManager.userData = loginData.user
                
                // Set gymId from newly created gym (owners)
                val targetGymId = loginData.user?.gyms?.firstOrNull()?.id
                sessionManager.gymId = targetGymId
                
                // Also update runtime client
                NetworkClient.authToken = loginData.access_token
                NetworkClient.currentUser = loginData.user
                NetworkClient.gymId = targetGymId

                
                Result.success(loginData)
            } else {
                Result.failure(Exception("Registration failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {

        return try {
            client.post(ApiEndpoints.LOGOUT)
            sessionManager.clear()
            NetworkClient.authToken = null
            NetworkClient.currentUser = null
            Result.success(Unit)
        } catch (e: Exception) {
            println("APP_LOG: Logout API failed, clearing local session anyway: ${e.message}")
            sessionManager.clear()
            NetworkClient.authToken = null
            NetworkClient.currentUser = null
            Result.success(Unit) // Still return success to allow local logout
        }
    }
}
