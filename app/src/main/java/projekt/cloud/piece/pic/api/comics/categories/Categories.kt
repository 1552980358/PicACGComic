package projekt.cloud.piece.pic.api.comics.categories

import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiConstants.PICA_COMIC_API_DOMAIN
import projekt.cloud.piece.pic.api.Header
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.util.CoroutineUtil.default
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.GET
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.getRequest

class Categories(private val token: String): BaseStringApiRequest<CategoriesResponseBody>() {
    
    private companion object {
        const val API_CATEGORIES = "categories"
    }
    
    override suspend fun requestApi(): HttpRequest {
        return withContext(io) {
            getRequest(
                PICA_COMIC_API_DOMAIN,
                API_CATEGORIES,
                Header.getHeader(API_CATEGORIES, GET, token)
            )
        }
    }
    
    override suspend fun responseBody(): CategoriesResponseBody {
        return withContext(default) { reflectInline(responseBody) }
    }
    
}