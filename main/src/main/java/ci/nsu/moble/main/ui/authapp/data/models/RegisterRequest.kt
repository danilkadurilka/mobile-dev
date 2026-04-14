package ci.nsu.moble.main.ui.authapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val login: String,
    val password: String,
    val email: String,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("roleId")
    val roleId: Int = 1,
    @SerialName("authAllowed")
    val authAllowed: Boolean = true,
    val person: PersonDto
)