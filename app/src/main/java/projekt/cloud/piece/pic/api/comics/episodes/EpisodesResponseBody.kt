package projekt.cloud.piece.pic.api.comics.episodes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpisodesResponseBody(val code: Int, val message: String, private val data: Data) {
    
    @Serializable
    data class Data(val eps: Episodes)
    
    val episodes: Episodes
        get() = data.eps
    
    operator fun get(order: Int): Episode {
        return episodes.episodeList.find { it.order == order }!!
    }
    
    val maxOrder: Int
        get() = episodes.episodeList.maxOf { it.order }
    
    @Serializable
    data class Episodes(
        @SerialName("docs")
        val episodeList: List<Episode>,
        val total: Int,
        val limit: Int,
        val page: Int,
        val pages: Int
    )
    
    @Serializable
    data class Episode(
        @SerialName("_id")
        val id: String,
        val title: String,
        val order: Int,
        @SerialName("updated_at")
        val updateDate: String,
        @SerialName("id")
        private val privateId: String
    )
    
}