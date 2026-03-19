package com.gym.gymapp.data.repository

import com.gym.gymapp.data.models.*
import com.gym.gymapp.network.ApiEndpoints
import com.gym.gymapp.network.NetworkClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class GymRepository {
    private val client = NetworkClient.client
    
    suspend fun getGyms(): Result<List<Gym>> {
        return try {
            val response: ApiResponse<List<Gym>> = client.get(ApiEndpoints.GYMS).body()
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDashboardStats(gymId: String): Result<DashboardStats> {
        return try {
            val response: ApiResponse<DashboardStats> = client.get(ApiEndpoints.DASHBOARD_STATS) {
                parameter("gymId", gymId)
            }.body()
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTodayAttendance(gymId: String): Result<List<Attendance>> {
        return try {
            val response: ApiResponse<List<Attendance>> = client.get(ApiEndpoints.ATTENDANCE_TODAY) {
                parameter("gymId", gymId)
            }.body()
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkIn(request: AttendanceCheckInRequest): Result<Attendance> {
        return try {
            val response: ApiResponse<Attendance> = client.post(ApiEndpoints.ATTENDANCE) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMembershipPlans(gymId: String): Result<List<MembershipPlan>> {
        return try {
            val response: ApiResponse<List<MembershipPlan>> = client.get(ApiEndpoints.PLANS) {
                parameter("gymId", gymId)
            }.body()
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
