package projekt.cloud.piece.pic.api.common

import kotlinx.serialization.Serializable

@Serializable
data class LikeResponseBody(val code: Int, val message: String, private val data: Data) {
    
    private companion object {
        const val ACTION_LIKE = "like"
        const val ACTION_UNLIKE = "unlike"
    }
    
    val isLiked: Boolean
        get() = data.action == ACTION_LIKE
    
    val isUnliked: Boolean
        get() = data.action == ACTION_UNLIKE
    
    @Serializable
    data class Data(
        val action: String
    )
    
}