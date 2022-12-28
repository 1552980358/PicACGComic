package projekt.cloud.piece.pic.api.base

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ErrorResponse.RejectedResponseBody
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.SerializeUtil.decodeJson

abstract class BaseStringApiRequest: BaseApiRequest() {
    
    private var _responseBody: String? = null
    val responseBody: String
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
    
    suspend inline fun <reified Body> reflectTo(): Body {
        return withContext(io) {
            responseBody.decodeJson()
        }
    }
    
}