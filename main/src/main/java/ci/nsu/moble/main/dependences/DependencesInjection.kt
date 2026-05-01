package ci.nsu.moble.main.dependences

import android.content.Context
import ci.nsu.moble.main.data.database.DepositAppDatabase
import ci.nsu.moble.main.data.database.DepositDAO
import ci.nsu.moble.main.data.network.ApiService
import ci.nsu.moble.main.data.network.NetworkModule
import ci.nsu.moble.main.data.repository.AuthRepository
import ci.nsu.moble.main.data.repository.DepositRepository
import ci.nsu.moble.main.data.storage.TokenManager

class DependencesInjection(context: Context)
{
    val tokenManager: TokenManager = TokenManager(context)
    private val database: DepositAppDatabase by lazy{
        DepositAppDatabase.getDb(context)
    }
    val depositDao: DepositDAO by lazy {
        database.dao()
    }
    val apiService: ApiService by lazy {
        NetworkModule.provideApiService(tokenManager)
    }
    val authRepository: AuthRepository by lazy {
        AuthRepository(apiService, tokenManager)
    }
    val depositRepository: DepositRepository by lazy {
        DepositRepository(depositDao)
    }
    val viewModelFactory: ViewModelFactory by lazy {
        ViewModelFactory(this)
    }
}