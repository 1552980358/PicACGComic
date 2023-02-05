package projekt.cloud.piece.pic.api.comics.comments.post

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.default
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.POST
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.postJsonRequest
import projekt.cloud.piece.pic.util.SerializeUtil.encodeJson

class Comments(private val token: String, private val id: String, private val content: String): BaseStringApiRequest<CommentsResponseBody>() {
    
    private companion object {
        const val API_COMMENTS_PREFIX = "comics/"
        const val API_COMMENTS_SUFFIX = "/comments"
    }
    
    override suspend fun requestApi(): HttpRequest {
        val path = API_COMMENTS_PREFIX + id + API_COMMENTS_SUFFIX
        return withContext(io) {
            postJsonRequest(
                PICA_COMIC_API_DOMAIN,
                path,
                Header.getHeader(path, POST, token),
                jsonRequestBody()
            )
        }
    }
    
    private suspend fun jsonRequestBody(): String {
        return withContext(default) {
            CommentsRequestBody(content).encodeJson()
        }
    }
    
    override suspend fun responseBody(): CommentsResponseBody {
        return withContext(default) { reflectInline(responseBody) }
    }
    
}