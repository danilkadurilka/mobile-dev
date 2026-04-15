package ci.nsu.moble.main.ui.authapp.data.repository

import ci.nsu.moble.main.ui.authapp.data.network.ApiService
import ci.nsu.moble.main.ui.authapp.data.models.GroupDto
import ci.nsu.moble.main.ui.authapp.data.models.RegisterRequest
import ci.nsu.moble.main.ui.authapp.data.models.UserDto
import ci.nsu.moble.main.ui.authapp.data.network.PublicApiService
import ci.nsu.moble.main.ui.authapp.data.storage.TokenManager
import retrofit2.HttpException
import java.io.IOException

class AuthRepository(
    private val apiService: ApiService,
    private val publicApiService: PublicApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(login: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(mapOf("login" to login, "password" to password))
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null && authResponse.token.isNotBlank()) {
                    tokenManager.saveToken(authResponse.token)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Токен не получен"))
                }
            } else {
                Result.failure(Exception("Ошибка авторизации: ${response.code()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("Ошибка авторизации: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Ошибка сети: проверьте подключение"))
        } catch (e: Exception) {
            Result.failure(Exception("Неизвестная ошибка: ${e.message}"))
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
            Result.failure(Exception("Ошибка регистрации: ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(Exception("Ошибка сети: проверьте подключение"))
        } catch (e: Exception) {
            Result.failure(Exception("Неизвестная ошибка: ${e.message}"))
        }
    }
    suspend fun getUsers(): Result<List<UserDto>> {
        return try {
            val response = apiService.getUsers()
            if (response.isSuccessful) {
                val users = response.body()
                if (users != null) {
                    Result.success(users)
                } else {
                    Result.failure(Exception("Пустой ответ от сервера"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Ошибка загрузки пользователей: ${response.code()} - $errorBody"))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Result.failure(Exception("Ошибка загрузки пользователей: ${e.message()} - $errorBody"))
        } catch (e: IOException) {
            Result.failure(Exception("Ошибка сети: проверьте подключение"))
        } catch (e: Exception) {
            Result.failure(Exception("Неизвестная ошибка: ${e.message}"))
        }
    }
    suspend fun getGroups(): Result<List<GroupDto>> {
        return try {
            val response = publicApiService.getGroups()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Ошибка загрузки групп: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка: ${e.message}"))
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }
}