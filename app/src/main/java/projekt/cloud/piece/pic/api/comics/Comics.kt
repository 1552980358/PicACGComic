package projekt.cloud.piece.pic.api.comics

import java.net.URLEncoder
import java.nio.charset.Charset
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.Sort
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.default
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.GET
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.asQueryStr
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.getRequest

class Comics private constructor(
    private val token: String, private val category: String, private val page: String, private val sort: String
): BaseStringApiRequest<ComicsResponseBody>() {
    
    private companion object {
        const val API_COMICS = "comics"
        const val API_COMICS_QUERY_PAGE = "page"
        const val API_COMICS_QUERY_CATEGORY = "c"
        const val API_COMICS_QUERY_SORT = "s"
    }
    
    constructor(token: String, category: String, page: Int, sort: Sort):
        this(token, category, page.toString(), sort.string)
    
    override suspend fun requestApi(): HttpRequest {
        @Suppress("BlockingMethodInNonBlockingContext")
        val query = mapOf(
            API_COMICS_QUERY_CATEGORY to URLEncoder.encode(category, Charset.defaultCharset().displayName()),
            API_COMICS_QUERY_PAGE to page,
            API_COMICS_QUERY_SORT to sort
        ).asQueryStr
        return withContext(io) {
            getRequest(
                PICA_COMIC_API_DOMAIN,
                API_COMICS,
                query,
                Header.getHeader(API_COMICS, query, GET, token)
            )
        }
    }
    
    override suspend fun responseBody(): ComicsResponseBody {
        return withContext(default) { reflectInline(responseBody) }
    }
    
}