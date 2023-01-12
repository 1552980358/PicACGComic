package projekt.cloud.piece.pic.api.auth

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.ErrorResponse.ErrorResponseBody
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.POST
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.postJsonRequest
import projekt.cloud.piece.pic.util.SerializeUtil.encodeJson

class SignIn(private val username: String, private val password: String): BaseStringApiRequest<SignInResponseBody>() {
    
    companion object SignInUtil {
        private const val API_SIGN_IN = "auth/sign-in"
        private const val SIGN_IN_ERROR_INVALID = 1004
    }
    
    override suspend fun responseBody(): SignInResponseBody {
        return reflectInline(responseBody)
    }
    
    override suspend fun requestApi(): HttpRequest {
        return withContext(io) {
            postJsonRequest(
                PICA_COMIC_API_DOMAIN,
                API_SIGN_IN,
                Header.getHeader(API_SIGN_IN, POST),
                SignInRequestBody(username, password).encodeJson()
            )
        }
    }
    
    fun isInvalid(errorResponseBody: ErrorResponseBody) =
        errorResponseBody.error == SIGN_IN_ERROR_INVALID
    
}