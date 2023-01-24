package projekt.cloud.piece.pic.api.comics

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_INT
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_STRING
import projekt.cloud.piece.pic.api.image.Image

@Serializable
data class ComicsResponseBody(val code: Int, val message: Int, private val data: Data) {
    
    @Serializable
    data class Data(val comics: Comics)
    
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
        @SerialName("thumb")
        val cover: Image,
        @SerialName("pagesCount")
        val images: Int,
        @SerialName("epsCount")
        val episodeSize: Int,
        @SerialName("categories")
        val categoryList: List<String>,
        val finished: Boolean,
        val likesCount: Int,
        @SerialName("id")
        private val privateId: String = IGNORE_STRING,
        val totalViews: Int = IGNORE_INT,
        val totalLikes: Int = IGNORE_INT, ) {
        
        val categories: String
            get() = categoryList.joinToString(separator = " ")
        
    }
    
}