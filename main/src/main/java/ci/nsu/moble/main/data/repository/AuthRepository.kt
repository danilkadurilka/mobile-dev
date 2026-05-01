package ci.nsu.moble.main.data.repository

import ci.nsu.moble.main.data.storage.TokenManager
import ci.nsu.moble.main.data.models.GroupDto
import ci.nsu.moble.main.data.models.RegisterRequest
import ci.nsu.moble.main.data.models.UserDto
import ci.nsu.moble.main.data.network.ApiService
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(login: String, password: String): Result<Pair<String, Long>> {  // ← возвращаем токен и userId
        return try {
            val response = apiService.login(mapOf("login" to login, "password" to password))
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null && authResponse.token.isNotBlank()) {
                    tokenManager.saveToken(authResponse.token)

                    // Получаем ID пользователя
                    val userId = getUserByLogin(login)
                    if (userId != null) {
                        tokenManager.saveUserId(userId)
                        Result.success(Pair(authResponse.token, userId))
                    } else {
                        Result.failure(Exception("Не удалось найти пользователя"))
                    }
                } else {
                    Result.failure(Exception("Токен не получен"))
                }
            } else {
                Result.failure(Exception("Ошибка авторизации: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка: ${e.message}"))
        }
    }

    private suspend fun getUserByLogin(login: String): Long? {
        var attempts = 0
        while (attempts < 3) {
            try {
                val response = apiService.getUsers()
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    val user = users.find {
                        it.login.equals(login, ignoreCase = true)
                    }
                    if (user != null) {
                        return user.userId.toLong()
                    }
                }
            } catch (e: Exception) {
                // Игнорируем, пробуем снова
            }
            attempts++
            delay(1000L * attempts)  // Экспоненциальная задержка
        }
        return null
    }
    suspend fun getGroups(): Result<List<GroupDto>> {
        return try {
            val response = apiService.getGroups()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Ошибка загрузки групп: ${response.code()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        } catch (e: IOException) {
            Result.failure(Exception("Нет подключения к интернету"))
        }
    }
    suspend fun getUsers(): Result<List<UserDto>> {
        return try {
            val response = apiService.getUsers()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Ошибка загрузки пользователей: ${response.code()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        } catch (e: IOException) {
            Result.failure(Exception("Нет подключения к интернету"))
        }
    }
    suspend fun register(request: RegisterRequest): Result<Unit> {
        return try {
            val response = apiService.register(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ошибка регистрации: ${response.code()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        } catch (e: IOException) {
            Result.failure(Exception("Нет подключения к интернету"))
        }
    }
}