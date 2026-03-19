package com.gym.gymapp.data.repository

import com.gym.gymapp.data.models.*
import com.gym.gymapp.network.NetworkClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class BroadcastRepository {
    private val client = NetworkClient.client

    suspend fun sendBroadcast(gymId: String, message: String): BroadcastResponse {
        val request = BroadcastRequest(gymId, message)
        val response = client.post("/broadcast/send") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        val apiResponse: ApiResponse<BroadcastResponse> = response.body()
        return apiResponse.data
    }

    suspend fun getLogs(gymId: String): List<BroadcastLog> {
        val response = client.get("/broadcast/logs") {
            parameter("gymId", gymId)
        }
        return if (response.status == HttpStatusCode.OK) {
            val apiResponse: ApiResponse<List<BroadcastLog>> = response.body()
            apiResponse.data
        } else {
            emptyList()
        }
    }
}
