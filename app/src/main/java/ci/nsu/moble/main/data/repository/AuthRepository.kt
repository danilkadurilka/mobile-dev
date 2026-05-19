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
    suspend fun login(login: String, password: String): Result<Pair<String, Long>>
    {
        return try
        {
            val response = apiService.login(mapOf("login" to login, "password" to password))
            if (response.isSuccessful)
            {
                val authResponse = response.body()
                if (authResponse != null && authResponse.token.isNotBlank())
                {
                    tokenManager.saveToken(authResponse.token)
                    val userId = findUserIdFast(login)
                    if (userId != null)
                    {
                        tokenManager.saveUserId(userId)
                        Result.success(Pair(authResponse.token, userId))
                    }
                    else
                    {
                        Result.failure(Exception("Не удалось найти пользователя"))
                    }
                }
                else
                {
                    Result.failure(Exception("Токен не получен"))
                }
            }
            else
            {
                Result.failure(Exception("Ошибка авторизации: ${response.code()}"))
            }
        }
        catch (e: Exception)
        {
            Result.failure(Exception("Ошибка: ${e.message}"))
        }
    }
    private suspend fun findUserIdFast(login: String): Long?
    {
        return try
        {
            val response = apiService.getUsers()
            if (response.isSuccessful)
            {
                val users = response.body() ?: emptyList()
                users.find { it.login.equals(login, ignoreCase = true) }?.userId?.toLong()
            }
            else
                null
        }
        catch (e: Exception)
        {
            null
        }
    }
    suspend fun getGroups(): Result<List<GroupDto>>
    {
        return try
        {
            val response = apiService.getGroups()
            if (response.isSuccessful)
            {
                Result.success(response.body() ?: emptyList())
            }
            else
            {
                Result.failure(Exception("Ошибка загрузки групп: ${response.code()}"))
            }
        }
        catch (e: HttpException)
        {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }
    suspend fun getUsers(): Result<List<UserDto>>
    {
        return try
        {
            val response = apiService.getUsers()
            if (response.isSuccessful)
            {
                Result.success(response.body() ?: emptyList())
            }
            else
            {
                Result.failure(Exception("Ошибка загрузки пользователей: ${response.code()}"))
            }
        }
        catch (e: HttpException)
        {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }
    suspend fun register(request: RegisterRequest): Result<Unit>
    {
        return try
        {
            val response = apiService.register(request)
            if (response.isSuccessful)
            {
                Result.success(Unit)
            }
            else
            {
                Result.failure(Exception("Ошибка регистрации: ${response.code()}"))
            }
        }
        catch (e: HttpException)
        {
            Result.failure(Exception("Ошибка сети: ${e.message}"))
        }
    }
    fun logout() {
        tokenManager.clearAll()
    }
}
