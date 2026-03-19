package com.gym.gymapp.data.repository

import com.gym.gymapp.data.models.MembershipPlan
import com.gym.gymapp.network.NetworkClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

import com.gym.gymapp.data.models.ApiResponse

class PlanRepository {
    private val client = NetworkClient.client

    suspend fun getPlans(gymId: String): List<MembershipPlan> {
        val response = client.get("/plans") {
            parameter("gymId", gymId)
        }
        return if (response.status == HttpStatusCode.OK) {
            val apiResponse: ApiResponse<List<MembershipPlan>> = response.body()
            apiResponse.data
        } else {
            emptyList()
        }
    }


    suspend fun createPlan(plan: MembershipPlan): Boolean {
        val response = client.post("/plans") {
            contentType(ContentType.Application.Json)
            setBody(plan)
        }
        return response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK
    }

    suspend fun updatePlan(planId: String, plan: MembershipPlan): Boolean {
        val response = client.patch("/plans/$planId") {
            contentType(ContentType.Application.Json)
            setBody(plan)
        }
        return response.status == HttpStatusCode.OK
    }
}

