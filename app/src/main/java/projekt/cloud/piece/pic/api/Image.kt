package projekt.cloud.piece.pic.api

import kotlinx.serialization.Serializable

object Image {

    private const val STATIC_IMAGE_CONNECTOR = "/static/"

    @Serializable
    data class ImageBody(
        val originalName: String,
        val path: String,
        val fileServer: String
    )

    val ImageBody.url: String
        get() = when {
            fileServer.contains(STATIC_IMAGE_CONNECTOR) -> fileServer + path
            else -> fileServer + STATIC_IMAGE_CONNECTOR + path
        }

}