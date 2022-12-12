package projekt.cloud.piece.pic.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.util.HttpUtil.httpGet

object CommonBody {
    
    private const val THUMB_URL_DIVIDER = "/static/"
    private val String.serverWithDivider: String
        get() = when {
            contains(THUMB_URL_DIVIDER) -> this
            else -> this + THUMB_URL_DIVIDER
        }
    
    @Serializable
    data class Image(val originalName: String,
                     val path: String,
                     val fileServer: String) {
        
        val bitmap: Bitmap?
            get() = httpGet(fileServer.serverWithDivider + path).response?.body?.byteStream()?.let {
                BitmapFactory.decodeStream(it)
            }
        
    }
    
    /**
     * {"code":"","error":"","message":"","detail":""}
     */
    @Serializable
    data class ErrorResponseBody(val code: Int, val error: Int, val message: String, val detail: String)

}