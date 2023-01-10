package projekt.cloud.piece.pic.api.comics.favourites

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.POST

class Favourite(private val token: String, val id: String): BaseStringApiRequest<FavouriteResponseBody>() {
    
    private companion object {
        const val API_FAVOURITE_PREFIX = "comics/"
        const val API_FAVOURITE_SUFFIX = "/favourite"
        const val API_FAVOURITE_BODY = "{}"
    }
    
    override suspend fun responseBody(): FavouriteResponseBody {
        return reflectInline(responseBody)
    }
    
    override suspend fun requestApi(): HttpRequest {
        val path = API_FAVOURITE_PREFIX + id + API_FAVOURITE_SUFFIX
        return withContext(io) {
            HttpRequest.postJsonRequest(
                PICA_COMIC_API_DOMAIN,
                path,
                Header.getHeader(path, requestMethod = POST, token = token),
                API_FAVOURITE_BODY
            )
        }
    }
    
}