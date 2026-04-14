package ci.nsu.moble.main.ui.authapp.data.network

import ci.nsu.moble.main.ui.authapp.data.models.AuthResponse
import ci.nsu.moble.main.ui.authapp.data.models.GroupDto
import ci.nsu.moble.main.ui.authapp.data.models.RegisterRequest
import ci.nsu.moble.main.ui.authapp.data.models.UserDto
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(
        @Body credentials: Map<String, String>
    ): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<Unit>

    @GET("users")
    suspend fun getUsers(): Response<List<UserDto>>

    @GET("groups")
    suspend fun getGroups(): Response<List<GroupDto>>
}