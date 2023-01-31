package projekt.cloud.piece.pic.api.comics.episode

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.default
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.asQueryStr
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.GET

class Episode(
    private val token: String,
    private val id: String,
    private val order: Int,
    private val page: Int
): BaseStringApiRequest<EpisodeResponseBody>() {
    
    private companion object {
        const val API_EPISODE_PREFIX = "comics/"
        const val API_EPISODE_SEPARATOR = "/order/"
        const val API_EPISODE_SUFFIX = "/pages"
        
        const val API_EPISODE_ARG_PAGE = "page"
    }
    
    override suspend fun responseBody(): EpisodeResponseBody {
        return withContext(default) { reflectInline(responseBody) }
    }
    
    override suspend fun requestApi(): HttpRequest {
        val path = API_EPISODE_PREFIX + id + API_EPISODE_SEPARATOR + order + API_EPISODE_SUFFIX
        val query = mapOf(API_EPISODE_ARG_PAGE to page.toString()).asQueryStr
        return withContext(io) {
            HttpRequest.getRequest(
                PICA_COMIC_API_DOMAIN,
                path, query,
                Header.getHeader(path, query, GET, token))
        }
    }
    
}