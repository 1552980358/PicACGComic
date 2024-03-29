package projekt.cloud.piece.pic.api.comics.metadata

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_STRING
import projekt.cloud.piece.pic.api.base.BaseDateBody
import projekt.cloud.piece.pic.api.base.Date
import projekt.cloud.piece.pic.api.image.Image

@Serializable
data class ComicMetadataResponseBody(val code: Int, val message: String, private val data: Data) {
    
    val comic: Comic
        get() = data.comic
    
    val creator: Creator
        get() = comic.creator
    
    @Serializable
    data class Data(val comic: Comic)
    
    @Serializable
    data class Comic(
        @SerialName("_id")
        val id: String,
        @SerialName("_creator")
        val creator: Creator,
        val title: String,
        val description: String = IGNORE_STRING,
        val thumb: Image,
        val author: String = IGNORE_STRING,
        val chineseTeam: String = IGNORE_STRING,
        @SerialName("categories")
        val categoryList: List<String>,
        @SerialName("tags")
        val tagList: List<String>,
        @SerialName("pagesCount")
        val images: Int,
        @SerialName("epsCount")
        val episodeSize: Int,
        val finished: Boolean,
        @SerialName("updated_at")
        val updateDate: Date,
        @SerialName("created_at")
        val createDate: Date,
        val allowDownload: Boolean,
        val allowComment: Boolean,
        val totalLikes: Int,
        val totalViews: Int,
        val totalComments: Int,
        val viewsCount: Int,
        val likesCount: Int,
        val commentsCount: Int,
        val isFavourite: Boolean,
        val isLiked: Boolean
    ): BaseDateBody() {
        
        val createDateStr = createDate.str
    
        val updateDateStr = updateDate.str
        
    }
    
    @Serializable
    data class Creator(
        @SerialName("_id")
        val id: String,
        val gender: String,
        val name: String,
        val slogan: String = IGNORE_STRING,
        val title: String = IGNORE_STRING,
        val verified: Boolean = false,
        val exp: Int,
        val level: Int,
        @SerialName("characters")
        val characterList: List<String>,
        val role: String,
        val avatar: Image? = null
    )
    
}