package projekt.cloud.piece.pic.api.comics.metadata

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.GET

class ComicMetadata(private val token: String, private val id: String): BaseStringApiRequest<ComicMetadataResponseBody>() {
    
    private companion object {
        const val API_COMICS_METADATA = "comics/"
    }
    
    override suspend fun responseBody(): ComicMetadataResponseBody {
        return reflectInline(responseBody)
    }
    
    override suspend fun requestApi(): HttpRequest {
        val path = API_COMICS_METADATA + id
        return withContext(io) {
            HttpRequest.getRequest(
                PICA_COMIC_API_DOMAIN,
                path,
                headers = Header.getHeader(
                    path, requestMethod = GET, token = token
                )
            )
        }
    }
    
}