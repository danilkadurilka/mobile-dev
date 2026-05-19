package ci.nsu.moble.domain.states

import ci.nsu.moble.domain.models.User

sealed class AuthState
{
    object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}