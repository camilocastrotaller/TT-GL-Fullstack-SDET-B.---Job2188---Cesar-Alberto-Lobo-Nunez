import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

// User model
@Serializable
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val address: Address
)

@Serializable
data class Address(val city: String)

class ApiClient(private val client: HttpClient) {
    suspend fun fetchUsers(): List<User> {
        try {
            val response: HttpResponse = client.get("https://jsonplaceholder.typicode.com/users")
            if (response.status == HttpStatusCode.OK) {
                return response.receive<List<User>>()
            } else {
                throw Exception("Failed to fetch users: ${response.status}")
            }
        } catch (e: Exception) {
            println("Error fetching users: ${e.message}")
            return emptyList()
        }
    }

    fun filterUsersByCity(users: List<User>, city: String): List<User> {
        return users.filter { it.address.city.equals(city, ignoreCase = true) }
    }
}

suspend fun main() {
    val client = HttpClient() {
        install(io.ktor.client.features.json.JsonFeature) {
            serializer = KotlinxSerializer(Json { prettyPrint = true; isLenient = true })
        }
    }

    val apiClient = ApiClient(client)

    val users = apiClient.fetchUsers()

    if (users.isNotEmpty()) {
        val filteredUsers = apiClient.filterUsersByCity(users, "Gwenborough")
        filteredUsers.forEach { println("User: ${it.name}, City: ${it.address.city}") }
    }

    client.close() 
}
