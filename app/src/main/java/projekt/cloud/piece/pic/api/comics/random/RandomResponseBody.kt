package projekt.cloud.piece.pic.api.comics.random

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_INT
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_STRING
import projekt.cloud.piece.pic.api.image.Image

@Serializable
data class RandomResponseBody(val code: Int, val message: String, private val data: Data) {

    private companion object
    
    val comicList: List<Comic>
        get() = data.comicList
    
    @Serializable
    data class Data(
        @SerialName("comics")
        val comicList: List<Comic>
    )
    
    @Serializable
    data class Comic(
        @SerialName("_id")
        val id: String,
        val title: String,
        val author: String = IGNORE_STRING,
        val thumb: Image,
        @SerialName("categories")
        val categoryList: List<String>,
        val totalViews: Int = IGNORE_INT,
        val totalLikes: Int = IGNORE_INT,
        val likesCount: Int,
        @SerialName("pagesCount")
        val imageCount: Int,
        val epsCount: Int,
        val finished: Boolean) {
        
        private companion object
        
        val categoryStr: String
            get() = categoryList.joinToString(separator = " ") { it }
        
    }
    
}