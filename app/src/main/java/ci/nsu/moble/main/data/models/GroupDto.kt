package ci.nsu.moble.main.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupDto(
    @SerialName("groupId")
    val groupId: Int,
    @SerialName("groupName")
    val groupName: String
)