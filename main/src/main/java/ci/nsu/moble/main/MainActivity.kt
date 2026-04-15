package ci.nsu.moble.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ci.nsu.moble.main.ui.authapp.NavGraph
import ci.nsu.moble.main.ui.authapp.data.network.NetworkModule
import ci.nsu.moble.main.ui.authapp.data.repository.AuthRepository
import ci.nsu.moble.main.ui.authapp.data.storage.TokenManager
import ci.nsu.moble.main.ui.theme.PracticeTheme

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager
    private lateinit var authRepository: AuthRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)
        tokenManager.clearToken()
        val apiService = NetworkModule.provideApiService(tokenManager)
        val publicApiService = NetworkModule.providePublicApiService()
        authRepository = AuthRepository(apiService, publicApiService, tokenManager)
        setContent {
            PracticeTheme {
                NavGraph(
                    authRepository = authRepository,
                    tokenManager = tokenManager
                )
            }
        }
    }
}