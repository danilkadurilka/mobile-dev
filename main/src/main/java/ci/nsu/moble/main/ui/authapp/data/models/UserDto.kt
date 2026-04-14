package ci.nsu.moble.main.ui.authapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int,
    val login: String,
    val email: String,
    @SerialName("phoneNumber")
    val phoneNumber: String,
    val person: PersonDto?
)