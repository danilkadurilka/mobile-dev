package ci.nsu.moble.main.ui.authapp.data.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String
)