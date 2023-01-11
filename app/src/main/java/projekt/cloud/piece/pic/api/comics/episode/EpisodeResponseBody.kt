package projekt.cloud.piece.pic.api.comics.episode

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.image.Image

@Serializable
class EpisodeResponseBody(val code: Int, val message: String, private val data: Data) {
    
    @Serializable
    data class Data(
        val pages: Pages,
        val ep: Episode
    )
    
    @Serializable
    data class Pages(
        @SerialName("docs")
        val episodeImageList: List<EpisodeImage>,
        val total: Int,
        val limit: Int,
        val page: Int,
        val pages: Int
    )
    
    @Serializable
    data class EpisodeImage(
        @SerialName("_id")
        val id: String,
        val media: Image,
        @SerialName("id")
        private val privateId: String
    )
    
    @Serializable
    data class Episode(
        @SerialName("_id")
        val id: String,
        val title: String
    )
    
    val episodeImageList: List<EpisodeImage>
        get() = data.pages.episodeImageList
    
    val pages: Int
        get() = data.pages.pages
    
}