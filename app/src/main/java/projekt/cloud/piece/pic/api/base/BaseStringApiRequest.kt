package projekt.cloud.piece.pic.api.base

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ErrorResponse.RejectedResponseBody
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.SerializeUtil.decodeJson

abstract class BaseStringApiRequest<ResponseBody>: BaseApiRequest() {
    
    private var _responseBody: String? = null
    protected val responseBody: String
        get() = _responseBody!!
    
    val isComplete: Boolean
        get() = httpRequest.isComplete
    
    fun isEmptyResponse(): Boolean {
        _responseBody = httpRequest.response.body.string()
        return _responseBody.isNullOrBlank()
    }
    
    suspend fun isRejected(): Boolean {
        return withContext(io) {
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