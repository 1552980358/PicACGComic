package projekt.cloud.piece.pic.api.base

import okhttp3.Response
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState

abstract class BaseApiRequest {
    
    companion object BaseApiRequestUtil {
        @JvmStatic
        suspend fun <R: BaseApiRequest> R.request() = apply {
            requestInternal()
        }
    }
    
    protected lateinit var httpRequest: HttpRequest
    
    val state: HttpRequestState
        get() = httpRequest.state
    
    val message: String?
        get() = httpRequest.exceptionMessage
    
    protected val isSuccessful: Boolean
        get() = httpRequest.response.isSuccessful
    
    protected abstract suspend fun requestApi(): HttpRequest
    
    private suspend fun requestInternal() {
        httpRequest = requestApi()
        if (httpRequest.isComplete) {
            httpRequest.response.use { response ->
                copyStreamData(response)
            }
        }
    }
    
    protected abstract fun copyStreamData(response: Response)
    
}