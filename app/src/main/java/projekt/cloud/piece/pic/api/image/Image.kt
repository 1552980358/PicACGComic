package projekt.cloud.piece.pic.api.image

import kotlinx.serialization.Serializable

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
    
    fun getUrl(): String {
        return domain + STATIC_IMAGE_CONNECTOR + path
    }
    
}