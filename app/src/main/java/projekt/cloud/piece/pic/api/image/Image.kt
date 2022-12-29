package projekt.cloud.piece.pic.api.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.getRequest

@Serializable
data class Image(val originalName: String, private val path: String, private val fileServer: String) {
    
    companion object {
        private const val STATIC_IMAGE_CONNECTOR = "/static/"
    }
    
    private val domain: String
        get() = when {
            fileServer.contains(STATIC_IMAGE_CONNECTOR) -> fileServer.replace(STATIC_IMAGE_CONNECTOR, "")
            else -> fileServer
        }
    
    suspend fun obtainBitmap(): Bitmap? {
        return withContext(io) {
            getRequest(domain, STATIC_IMAGE_CONNECTOR + path).let { httpRequest ->
                if (!httpRequest.isComplete) {
                    return@let null
                }
                httpRequest.response.use { response ->
                    if (!response.isSuccessful) {
                        return@use null
                    }
                    BitmapFactory.decodeStream(response.body.byteStream())
                }
            }
        }
    }
    
}