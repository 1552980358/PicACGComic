package projekt.cloud.piece.pic.api.comics.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_INT
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_STRING
import projekt.cloud.piece.pic.api.base.BaseDateBody
import projekt.cloud.piece.pic.api.image.Image

@Serializable
data class AdvancedSearchResponseBody(val code: Int, val message: String, private val data: Data) {
    
    private companion object
    
    @Serializable
    data class Data(val comics: Comics)
    
    val comicList: List<Comic>
        get() = data.comics.comicList
    
    val total: Int
        get() = data.comics.total
    
    val page: Int
        get() = data.comics.page
    
    val pages: Int
        get() = data.comics.pages
    
    @Serializable
    data class Comics(
        @SerialName("docs")
        val comicList: List<Comic>,
        val total: Int,
        val limit: Int,
        val page: Int,
        val pages: Int
    )
    
    @Serializable
    data class Comic(
        @SerialName("_id")
        val id: String,
        val title: String,
        val author: String = IGNORE_STRING,
        val description: String = IGNORE_STRING,
        val chineseTeam: String = IGNORE_STRING,
        @SerialName("categories")
        val categoryList: List<String>,
        @SerialName("tags")
        val tagList: List<String>,
        @SerialName("created_at")
        val createDate: String,
        @SerialName("updated_at")
        val updateDate: String,
        @SerialName("thumb")
        val cover: Image,
        val finished: Boolean,
        val likesCount: Int,
        val totalViews: Int = IGNORE_INT,
        val totalLikes: Int = IGNORE_INT
    ): BaseDateBody() {
        
        private companion object
        
        val createDateStr = createDate.str
        val updateDateStr = updateDate.str
        
    }
    
}