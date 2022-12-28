package projekt.cloud.piece.pic.api.collections

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.GET

class Collections(private val token: String): BaseStringApiRequest<CollectionsResponseBody>() {
    
    private companion object {
        const val API_COLLECTIONS = "collections"
    }
    
    override suspend fun requestApi(): HttpRequest {
        return withContext(io) {
            HttpRequest.getRequest(
                PICA_COMIC_API_DOMAIN,
                API_COLLECTIONS,
                headers = Header.getHeader(
                    PICA_COMIC_API_DOMAIN, requestMethod = GET, token = token
                )
            )
        }
    }
    
    override suspend fun responseBody(): CollectionsResponseBody {
        return reflectInline(responseBody)
    }
    
}