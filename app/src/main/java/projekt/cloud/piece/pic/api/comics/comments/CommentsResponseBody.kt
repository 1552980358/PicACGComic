package projekt.cloud.piece.pic.api.comics.comments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_INT
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_STRING
import projekt.cloud.piece.pic.api.base.BaseDateBody
import projekt.cloud.piece.pic.api.base.Date
import projekt.cloud.piece.pic.api.image.Image

@Serializable
data class CommentsResponseBody(val code: Int, val message: String, private val data: Data) {
    
    val pages: Int
        get() = data.comments.pages
    
    val page: Int
        get() = data.comments.page
    
    val topCommentList: List<Comment>
        get() = data.topCommentList
    
    val commentList: List<Comment>
        get() = data.comments.commentList
    
    @Serializable
    data class Data(
        val comments: Comments,
        @SerialName("topComments")
        val topCommentList: List<Comment> = listOf()
    )
    
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
        @SerialName("_comic")
        val comic: String,
        @SerialName("_id")
        val id: String,
        @SerialName("_user")
        val user: User,
        @SerialName("content")
        val comment: String,
        var isLiked: Boolean,
        val likesCount: Int,
        val commentsCount: Int,
        val totalComments: Int = IGNORE_INT,
        @SerialName("created_at")
        val createDate: Date,
        val isTop: Boolean,
        val hide: Boolean,
        @SerialName("id")
        private val privateId: String = IGNORE_STRING
    ): BaseDateBody() {
        
        val createDateStr: String
            get() = createDate.str
    
    }
    
    @Serializable
    data class User(
        @SerialName("_id")
        val id: String,
        val name: String,
        val avatar: Image? = null,
        val gender: String,
        val slogan: String = IGNORE_STRING,
        val character: String = IGNORE_STRING,
        @SerialName("characters")
        val characterList: List<String>,
        val title: String = IGNORE_STRING,
        val role: String,
        val verified: Boolean = false,
        val exp: Int,
        val level: Int,
    )

}