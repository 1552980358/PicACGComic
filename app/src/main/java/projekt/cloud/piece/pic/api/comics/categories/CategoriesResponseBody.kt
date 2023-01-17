package projekt.cloud.piece.pic.api.comics.categories

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_STRING
import projekt.cloud.piece.pic.api.image.Image

@Serializable
class CategoriesResponseBody(val code: Int, val message: String, private val data: Data) {
    
    private companion object
    
    val categoryList: List<Category>
        get() = data.categoryList
    
    @Serializable
    data class Data(@SerialName("categories") val categoryList: List<Category>)

    @Serializable
    data class Category(
        @SerialName("_id")
        val id: String = IGNORE_STRING,
        val title: String,
        val description: String = IGNORE_STRING,
        val thumb: Image,
        val isWeb: Boolean = false,
        val active: Boolean = true,
        /** Useless **/
        private val link: String = IGNORE_STRING
    )
    
}