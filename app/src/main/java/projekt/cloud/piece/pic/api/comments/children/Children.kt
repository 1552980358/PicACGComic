package projekt.cloud.piece.pic.api.comments.children

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.default
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.GET
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.asQueryStr
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.getRequest

/**
 * Don't call it as `Childrens`, it is grammatically incorrect!!!
 **/
class Children(private val token: String, private val id: String, private val page: String): BaseStringApiRequest<ChildrenResponseBody>() {
    
    constructor(token: String, id: String, page: Int): this(token, id, page.toString())
    
    private companion object {
        const val API_CHILDREN_PREFIX = "comments/"
        const val API_CHILDREN_SUFFIX = "/children" + 's'
        const val API_CHILDREN_ARG_PAGE = "page"
    }
    
    override suspend fun requestApi(): HttpRequest {
        val path = API_CHILDREN_PREFIX + id + API_CHILDREN_SUFFIX
        val query = mapOf(API_CHILDREN_ARG_PAGE to page).asQueryStr
        return withContext(io) {
            getRequest(
                PICA_COMIC_API_DOMAIN,
                path,
                query,
                Header.getHeader(path, query, GET, token)
            )
        }
    }
    
    override suspend fun responseBody(): ChildrenResponseBody {
        return withContext(default) { reflectInline(responseBody) }
    }
    
}