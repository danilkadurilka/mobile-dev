package ci.nsu.moble.main.data.network

import ci.nsu.moble.main.data.storage.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response
    {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Content-Type", "application/json")
        tokenManager.getToken()?.let{ token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }
        return chain.proceed(requestBuilder.build())
    }
}