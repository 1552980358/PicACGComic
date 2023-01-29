package projekt.cloud.piece.pic.api.comics.comments

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.GET
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.asQueryStr
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.getRequest

class Comments private constructor(
    private val token: String, private val id: String, private val page: String
): BaseStringApiRequest<CommentsResponseBody>() {
    
    constructor(token: String, id: String, page: Int): this(token, id, page.toString())
    
    private companion object {
        const val API_COMMENTS_PREFIX = "comics/"
        const val API_COMMENTS_SUFFIX = "/comments"
        const val API_COMMENTS_ARG_PAGE = "page"
    }
    
    override suspend fun requestApi(): HttpRequest {
        val path = API_COMMENTS_PREFIX + id + API_COMMENTS_SUFFIX
        val query = mapOf(API_COMMENTS_ARG_PAGE to page).asQueryStr
        return withContext(io) {
            getRequest(
                PICA_COMIC_API_DOMAIN,
                path,
                query,
                Header.getHeader(path, query, GET, token)
            )
        }
    }
    
    override suspend fun responseBody(): CommentsResponseBody {
        return withContext(io) { reflectInline(responseBody) }
    }
    
}