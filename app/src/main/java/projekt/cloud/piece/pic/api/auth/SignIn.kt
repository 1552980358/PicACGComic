package projekt.cloud.piece.pic.api.auth

import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.POST
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.postJsonRequest
import projekt.cloud.piece.pic.util.SerializeUtil.encodeJson

object SignIn {

    private const val SIGN_IN_API = "auth/sign-in"

    @Serializable
    data class SignInRequestBody(
        val email: String,
        val password: String
    )

    @Serializable
    data class SignInResponseBody(
        val code: Int,
        val message: String,
        @SerialName("data")
        private val data: Data) {

        val token: String
            get() = data.token

        @Serializable
        data class Data(val token: String)

    }

    suspend fun signIn(username: String, password: String): HttpRequest {
        return withContext(io) {
            postJsonRequest(
                PICA_COMIC_API_DOMAIN,
                SIGN_IN_API,
                Header.getHeader(SIGN_IN_API, requestMethod = POST),
                SignInRequestBody(username, password).encodeJson()
            )
        }
    }

}