package projekt.cloud.piece.pic.api.auth

import kotlinx.serialization.Serializable

@Serializable
data class SignInResponseBody(val code: Int,
                              val message: String,
                              private val data: Data) {
    
    val token: String
        get() = data.token
    
    @Serializable
    data class Data(val token: String)
    
}