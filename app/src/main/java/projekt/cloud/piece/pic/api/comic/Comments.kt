package projekt.cloud.piece.pic.api.comic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.CommonBody.Image

object Comments {

    private const val API_COMIC_COMMENT_PREFIX = "comics/"
    private const val API_COMIC_COMMENT_SUFFIX = "/comments"
    
    private fun getCommentsMethod(id: String) =
        API_COMIC_COMMENT_PREFIX + API_COMIC_COMMENT_SUFFIX
    
    @Serializable
    data class ComicCommentsResponseBody(val code: Int, val message: String, val data: Data) {
        
        @Serializable
        data class Data(val comments: Comments, val topComments: List<Doc>) {
            
            @Serializable
            data class Comments(val docs: List<Doc>, val total: Int, val limit: Int, val page: Int, val pages: Int)
    
            @Serializable
            data class Doc(@SerialName("_id") val id: String,
                           val content: String,
                           @SerialName("_user") val user: User,
                           @SerialName("comic") val _comic: String,
                           val totalComments: Int,
                           val isTop: Boolean,
                           val hide: Boolean,
                           @SerialName("created_at") val createDate: String,
                           @SerialName("id") private val hiddenId: String = "",
                           val likesCount: Int,
                           val commentsCount: Int,
                           val isLiked: Boolean) {
                
                @Serializable
                data class User(
                    @SerialName("_id") val id: String,
                    val gender: String,
                    val name: String,
                    val avatar: Image? = null,
                    val slogan: String = "",
                    val character: String = "",
                    val verified: Boolean,
                    val exp: Int,
                    val level: Int,
                    val characters: List<String>,
                    val role: String
                )
                
            }
            
        }
        
    }
    
}