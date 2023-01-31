package projekt.cloud.piece.pic.api.base

import kotlinx.coroutines.withContext
import okhttp3.Response
import projekt.cloud.piece.pic.api.ErrorResponse.ErrorResponseBody
import projekt.cloud.piece.pic.api.ErrorResponse.RejectedResponseBody
import projekt.cloud.piece.pic.util.CoroutineUtil.default
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.SerializeUtil.decodeJson

abstract class BaseStringApiRequest<ResponseBody>: BaseApiRequest() {
    
    private var _responseBody: String? = null
    protected val responseBody: String
        get() = _responseBody!!
    
    val isComplete: Boolean
        get() = httpRequest.isComplete
    
    override fun copyStreamData(response: Response) {
        _responseBody = response.body.string()
    }
    
    val isErrorResponse: Boolean
        get() = !isSuccessful
    
    suspend fun errorResponse(): ErrorResponseBody {
        return withContext(io) {
            responseBody.decodeJson()
        }
    }
    
    val isEmptyResponse: Boolean
        get() = _responseBody.isNullOrBlank()
    
    suspend fun isRejected(): Boolean {
        return withContext(default) {
            runCatching { responseBody.decodeJson<RejectedResponseBody>() }
                .getOrNull() != null
        }
    }
    
    abstract suspend fun responseBody(): ResponseBody
    
    protected suspend inline fun <reified Body> reflectInline(responseBody: String): Body {
        return withContext(io) {
            responseBody.decodeJson()
        }
    }
    
}