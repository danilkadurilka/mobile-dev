package ci.nsu.moble.main.ui.authapp.data.network

import ci.nsu.moble.main.ui.authapp.data.models.GroupDto
import retrofit2.Response
import retrofit2.http.GET

interface PublicApiService {
    @GET("groups")
    suspend fun getGroups(): Response<List<GroupDto>>
}