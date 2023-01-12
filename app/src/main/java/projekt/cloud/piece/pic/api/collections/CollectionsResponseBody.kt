package projekt.cloud.piece.pic.api.collections

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_STRING
import projekt.cloud.piece.pic.api.image.Image

@Serializable
data class CollectionsResponseBody(val code: Int, val message: String, private val data: Data) {
    
    private companion object;
    
    operator fun get(index: Int) = data.collections[index]
    
    val size: Int
        get() = data.collections.size
    
    @Serializable
    data class Data(val collections: List<Collection>)
    
    @Serializable
    data class Collection(val title: String, @SerialName("comics") val comicList: List<Comic>) {
        private companion object
    }
    
    @Serializable
    data class Comic(
        @SerialName("_id") val id: String,
        val title: String,
        val author: String = IGNORE_STRING,
        @SerialName("thumb")
        val cover: Image,
        @SerialName("categories")
        val categoryList: List<String>,
        @SerialName("epsCount")
        val episodeSize: Int,
        @SerialName("pagesCount")
        val pages: Int,
        val finished: Boolean,
        val totalViews: Int,
        val totalLikes: Int) {
    
        private companion object
        
        val categoryStr: String
            get() = categoryList.joinToString(separator = " ") { it }
        
    }
    
}