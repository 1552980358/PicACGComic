package projekt.cloud.piece.pic.api.comics.search

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.Sort
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.POST
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.asQueryStr
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.postJsonRequest
import projekt.cloud.piece.pic.util.SerializeUtil.encodeJson

class AdvancedSearch private constructor(
    private val token: String,
    /** Post Body **/
    private val categories: List<String>,
    private val keyword: String,
    private val sort: String,
    /** Query **/
    private val page: String
): BaseStringApiRequest<AdvancedSearchResponseBody>() {
    
    constructor(token: String, category: List<String>, keyword: String, sort: Sort, page: Int):
        this(token, category, keyword, sort.string, page.toString())
    
    private companion object {
        const val API_SEARCH_PATH = "comics/advanced-search"
        const val API_SEARCH_QUERY_PAGE = "page"
    }
    
    override suspend fun requestApi(): HttpRequest {
        val query = mapOf(API_SEARCH_QUERY_PAGE to page).asQueryStr
        return withContext(io) {
            postJsonRequest(
                PICA_COMIC_API_DOMAIN,
                API_SEARCH_PATH,
                query,
                Header.getHeader(API_SEARCH_PATH, query, POST, token),
                AdvancedSearchRequestBody(categories, keyword, sort).encodeJson()
            )
        }
    }
    
    override suspend fun responseBody(): AdvancedSearchResponseBody {
        return withContext(io) { reflectInline(responseBody) }
    }
    
}