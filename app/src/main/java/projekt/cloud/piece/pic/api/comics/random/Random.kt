package projekt.cloud.piece.pic.api.comics.random

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.default
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.GET

class Random(private val token: String): BaseStringApiRequest<RandomResponseBody>() {

    private companion object {
        const val API_RANDOM_PATH = "comics/random"
    }
    
    override suspend fun responseBody(): RandomResponseBody {
        return withContext(default) { reflectInline(responseBody) }
    }
    
    override suspend fun requestApi(): HttpRequest {
        return withContext(io) {
            HttpRequest.getRequest(
                PICA_COMIC_API_DOMAIN,
                API_RANDOM_PATH,
                Header.getHeader(API_RANDOM_PATH, GET, token)
            )
        }
    }
    
}