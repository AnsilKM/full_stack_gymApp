import io.ktor.client.*
import io.ktor.client.plugins.*

fun main() {
    HttpClient {
        HttpResponseValidator {
            validateResponse { response -> 
            }
        }
    }
}
