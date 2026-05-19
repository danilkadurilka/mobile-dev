package ci.nsu.moble.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val userId: Long,
    val login: String,
    val email: String,
    val phoneNumber: String?,
    val roleId: Int,
    val authAllowed: Boolean
)

@Serializable
data class Person(
    val firstName: String,
    val lastName: String,
    val middleName: String = "",
    val birthDate: String = "",
    val gender: String = "",
    val groupId: Int = 0
)

@Serializable
data class Group(
    val groupId: Int,
    val groupName: String
)

@Serializable
data class RegisterData(
    val login: String,
    val password: String,
    val email: String,
    val phoneNumber: String,
    val roleId: Int = 1,
    val authAllowed: Boolean = true,
    val person: Person
)