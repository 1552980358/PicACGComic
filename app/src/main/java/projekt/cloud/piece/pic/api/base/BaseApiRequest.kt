package projekt.cloud.piece.pic.api.base

import projekt.cloud.piece.pic.util.HttpRequest

abstract class BaseApiRequest {
    
    companion object {
        @JvmStatic
        suspend fun <R: BaseApiRequest> R.request() = apply {
            requestInternal()
        }
    }
    
    protected lateinit var httpRequest: HttpRequest
    
    protected abstract suspend fun requestApi(): HttpRequest
    
    private suspend fun requestInternal() {
        httpRequest = requestApi()
    }
    
}