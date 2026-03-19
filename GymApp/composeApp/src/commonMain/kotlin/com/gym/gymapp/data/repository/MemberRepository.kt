package com.gym.gymapp.data.repository

import com.gym.gymapp.data.models.*
import com.gym.gymapp.network.ApiEndpoints
import com.gym.gymapp.network.NetworkClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*

class MemberRepository {
    private val client = NetworkClient.client
    
    suspend fun getMembers(gymId: String? = null): Result<List<Member>> {
        return try {
            val response: ApiResponse<List<Member>> = client.get(ApiEndpoints.MEMBERS) {
                if (gymId != null) {
                    parameter("gymId", gymId)
                }
            }.body()
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createMember(request: CreateMemberRequest): Result<Member> {
        return try {
            val response = client.post(ApiEndpoints.MEMBERS) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status == HttpStatusCode.Created || response.status == HttpStatusCode.OK) {
                val apiRes: ApiResponse<Member> = response.body()
                Result.success(apiRes.data)
            } else {
                Result.failure(Exception("Failed to create member"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadImage(imageBytes: ByteArray): Result<String> {
        return try {
            val response: io.ktor.client.statement.HttpResponse = client.submitFormWithBinaryData(
                url = "${ApiEndpoints.MEMBERS}/upload",
                formData = formData {
                    append("file", imageBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                    })
                }
            )
            
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.Created) {
                val apiRes: ApiResponse<Map<String, String>> = response.body()
                Result.success(apiRes.data["url"] ?: "")
            } else {
                Result.failure(Exception("Image upload failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMember(id: String, request: UpdateMemberRequest): Result<Member> {
        return try {
            val response = client.put("${ApiEndpoints.MEMBERS}/$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            if (response.status == HttpStatusCode.OK) {
                val apiRes: ApiResponse<Member> = response.body()
                Result.success(apiRes.data)
            } else {
                Result.failure(Exception("Failed to update member"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMember(id: String): Result<Boolean> {
        return try {
            val response = client.delete("${ApiEndpoints.MEMBERS}/$id")
            if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NoContent) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to delete member"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
