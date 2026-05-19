package ci.nsu.moble.domain.interfaces

import ci.nsu.moble.domain.models.User
import ci.nsu.moble.domain.states.AuthState
import kotlinx.coroutines.flow.Flow

interface AuthManager
{
    fun getCurrentUser(): User?
    fun isLoggedIn(): Boolean
    suspend fun logout()
    fun observeAuthState(): Flow<AuthState>
}