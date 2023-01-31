package projekt.cloud.piece.pic.api.comics.like

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.common.LikeResponseBody
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.default
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.POST
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.postJsonRequest

class Like(private val token: String, val id: String): BaseStringApiRequest<LikeResponseBody>() {
    
    private companion object {
        const val API_LIKE_PREFIX = "comics/"
        const val API_LIKE_SUFFIX = "/like"
        const val API_LIKE_POST_BODY = "{}"
    }
    
    override suspend fun requestApi(): HttpRequest {
        val path = API_LIKE_PREFIX + id + API_LIKE_SUFFIX
        return withContext(io) {
            postJsonRequest(
                PICA_COMIC_API_DOMAIN,
                path,
                Header.getHeader(path, POST, token),
                API_LIKE_POST_BODY
            )
        }
    }
    
    override suspend fun responseBody(): LikeResponseBody {
        return withContext(default) { reflectInline(responseBody) }
    }
    
}