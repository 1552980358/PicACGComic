package projekt.cloud.piece.pic.api.comics.episodes

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.asQueryStr
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.GET

class Episodes(private val token: String, private val id: String, private val page: Int): BaseStringApiRequest<EpisodesResponseBody>() {
    
    companion object {
        private const val API_EPISODES_PREFIX = "comics/"
        private const val API_EPISODES_SUFFIX = "/eps"
        private const val API_EPISODES_ARG_PAGE = "page"
    }
    
    override suspend fun responseBody(): EpisodesResponseBody {
        return reflectInline(responseBody)
    }
    
    override suspend fun requestApi(): HttpRequest {
        val path = API_EPISODES_PREFIX + id + API_EPISODES_SUFFIX
        val query = mapOf(API_EPISODES_ARG_PAGE to page.toString()).asQueryStr
        return withContext(io) {
            HttpRequest.getRequest(
                PICA_COMIC_API_DOMAIN,
                path,
                query,
                Header.getHeader(path, query, GET, token)
            )
        }
    }
    
}