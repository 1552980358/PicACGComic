package projekt.cloud.piece.pic.api.comics.episodes

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.base.BaseDateBody
import projekt.cloud.piece.pic.api.base.Date

@Serializable
data class EpisodesResponseBody(val code: Int, val message: String, private val data: Data) {
    
    @Serializable
    data class Data(val eps: Episodes)
    
    val episodes: Episodes
        get() = data.eps
    
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
        val updateDate: Date,
        @SerialName("id")
        private val privateId: String
    ): BaseDateBody() {
        
        val updateDateStr: String
            get() = updateDate.str
    
    }
    
}