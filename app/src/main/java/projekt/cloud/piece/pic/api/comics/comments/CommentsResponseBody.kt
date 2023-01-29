package projekt.cloud.piece.pic.api.comics.comments

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_STRING
import projekt.cloud.piece.pic.api.base.BaseDateBody
import projekt.cloud.piece.pic.api.base.Date
import projekt.cloud.piece.pic.api.image.Image

@Serializable
data class CommentsResponseBody(val code: Int, val message: String, private val data: Data) {
    
    @Serializable
    data class Data(
        val comments: Comments,
        @SerialName("topComments")
        val topCommentList: List<Comment>? = null
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
        val isLiked: Int,
        val totalComments: Int,
        val likesCount: Int,
        val commentsCount: Int,
        @SerialName("created_at")
        val createDate: Date,
        val isTop: Boolean,
        val hide: Boolean,
        @SerialName("id")
        private val privateId: String
    ): BaseDateBody() {
        
        val createDateStr: String
            get() = createDate.str
    
    }
    
    @Serializable
    data class User(
        @SerialName("_id")
        val id: String,
        val name: String,
        val gender: String,
        val slogan: String = IGNORE_STRING,
        @SerialName("characters")
        val characterList: List<String>,
        val title: String = IGNORE_STRING,
        val role: String,
        val verified: Boolean,
        val exp: Int,
        val level: Int,
        val avatar: Image
    )

}