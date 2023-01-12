package projekt.cloud.piece.pic.api.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInRequestBody(
    @SerialName("email")
    val username: String,
    val password: String
)