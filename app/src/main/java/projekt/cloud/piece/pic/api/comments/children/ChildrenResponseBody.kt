package projekt.cloud.piece.pic.api.comments.children

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_INT
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_STRING
import projekt.cloud.piece.pic.api.base.BaseDateBody
import projekt.cloud.piece.pic.api.image.Image

/**
 * Originally should be ChildrensResponseBody, but grammatically incorrect.
 * So, ChildrensResponseBody -> ChildrenResponseBody
 **/
@Serializable
data class ChildrenResponseBody(val code: Int, val message: String, private val data: Data) {

    val pages: Int
        get() = data.comments.pages
    
    val commentList: List<Comment>
        get() = data.comments.commentList
    
    @Serializable
    data class Data(val comments: Comments)
    
    @Serializable
    data class Comments(
        @SerialName("docs")
        val commentList: List<Comment>,
        val total: Int,
        val limit: Int,
        val page: Int,
        val pages: Int
    )
    
    @Serializable
    data class Comment(
        @SerialName("_id")
        val id: String,
        @SerialName("content")
        val comment: String,
        @SerialName("_user")
        val user: User,
        @SerialName("_comic")
        val comic: String,
        @SerialName("_parent")
        val replyTo: String,
        var isLiked: Boolean,
        @SerialName("created_at")
        val createDate: String,
        val likesCount: Int,
        val isTop: Boolean,
        val hide: Boolean,
        val totalComments: Int = IGNORE_INT,
        @SerialName("id")
        private val privateId: String
    )
    
    @Serializable
    data class User(
        @SerialName("_id")
        val id: String,
        val name: String,
        val gender: String,
        val exp: Int,
        val level: Int,
        val avatar: Image? = null,
        @SerialName("characters")
        val characterList: List<String>,
        val title: String = IGNORE_STRING,
        val role: String = IGNORE_STRING,
        val character: String = IGNORE_STRING,
        val slogan: String = IGNORE_STRING,
        val verified: Boolean
    )

}