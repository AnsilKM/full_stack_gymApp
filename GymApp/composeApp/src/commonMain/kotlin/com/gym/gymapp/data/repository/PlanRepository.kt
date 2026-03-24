package com.gym.gymapp.data.repository

import com.gym.gymapp.data.models.MembershipPlan
import com.gym.gymapp.network.NetworkClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

import com.gym.gymapp.data.models.ApiResponse
import com.gym.gymapp.ui.components.NotificationManager
import com.gym.gymapp.ui.components.AppNotificationType

class PlanRepository {
    private val client = NetworkClient.client

    suspend fun getPlans(gymId: String): List<MembershipPlan> {
        val response = client.get("/plans") {
            parameter("gymId", gymId)
        }
        return if (response.status == HttpStatusCode.OK) {
            val apiResponse: ApiResponse<List<MembershipPlan>> = response.body()
            if (!apiResponse.status) {
                NotificationManager.showNotification(apiResponse.message, AppNotificationType.ERROR)
                emptyList()
            } else {
                apiResponse.data
            }
        } else {
            emptyList()
        }
    }


    suspend fun createPlan(plan: MembershipPlan): Boolean {
        val response = client.post("/plans") {
            contentType(ContentType.Application.Json)
            setBody(plan)
        }
        if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
            val apiResponse: ApiResponse<MembershipPlan> = response.body()
            if (!apiResponse.status) {
                NotificationManager.showNotification(apiResponse.message, AppNotificationType.ERROR)
            }
            return apiResponse.status
        }
        return false
    }

    suspend fun updatePlan(planId: String, plan: MembershipPlan): Boolean {
        val response = client.patch("/plans/$planId") {
            contentType(ContentType.Application.Json)
            setBody(plan)
        }
        return response.status == HttpStatusCode.OK
    }

    suspend fun deletePlan(planId: String): Boolean {
        if (planId.isBlank()) return false
        val response = client.delete("/plans/$planId")
        return response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NoContent
    }
}
