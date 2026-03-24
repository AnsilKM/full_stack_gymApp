package com.gym.gymapp.network

import com.gym.gymapp.getPlatform
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.json.Json
import kotlin.time.TimeSource

object NetworkClient {
    var authToken: String? = null
    var currentUser: com.gym.gymapp.data.models.UserDto? = null
    var gymId: String? = null


    // Event flow for global auth events (like session expiry)
    private val _authEvents = MutableSharedFlow<AuthEvent>(extraBufferCapacity = 1)
    val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()

    sealed class AuthEvent {
        object SessionExpired : AuthEvent()
    }

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        
        HttpResponseValidator {
            validateResponse { response ->
                if (response.status == HttpStatusCode.Unauthorized) {
                    // Ignore 401 on login screen requests
                    if (!response.call.request.url.encodedPath.contains("auth/login")) {
                        println("APP_LOG: 401 Unauthorized detected! Emitting SessionExpired event.")
                        _authEvents.tryEmit(AuthEvent.SessionExpired)
                    }
                }
            }
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                private var requestUrl = ""
                private var mark = TimeSource.Monotonic.markNow()
                private var currentMethod = ""
                
                override fun log(message: String) {
                    when {
                        message.startsWith("REQUEST:") -> {
                            requestUrl = message.substringAfter("REQUEST:").trim()
                            mark = TimeSource.Monotonic.markNow()
                        }
                        message.startsWith("METHOD:") -> {
                            currentMethod = message.substringAfter("METHOD:").trim()
                            println("HTTP --> $currentMethod $requestUrl")
                        }
                        message.startsWith("RESPONSE:") -> {
                            val status = message.substringAfter("RESPONSE:").trim()
                            val duration = mark.elapsedNow().inWholeMilliseconds
                            println("HTTP <-- $status $requestUrl (${duration}ms)")
                        }
                        message.startsWith("->") -> {
                            println("     ${message.substringAfter("->").trim()}")
                        }
                        message.contains("BODY START") -> { }
                        message.contains("BODY END") -> {
                            if (message.contains("RESPONSE")) {
                                println("HTTP <-- END HTTP")
                            } else {
                                println("HTTP --> END $currentMethod")
                            }
                        }
                        message == "COMMON HEADERS" || message == "CONTENT HEADERS" -> { }
                        else -> {
                            if (message.isNotBlank()) {
                                if (message.trim().startsWith("{") || message.trim().startsWith("[")) {
                                     println(message)
                                } else {
                                     println("     $message")
                                }
                            }
                        }
                    }
                }
            }
        }
        
        defaultRequest {
            url(BASE_URL)
            authToken?.let {
                header("Authorization", "Bearer $it")
            }
            gymId?.let {
                header("x-gym-id", it)
            }
        }
    }


    val BASE_URL = getPlatform().baseUrl
}
