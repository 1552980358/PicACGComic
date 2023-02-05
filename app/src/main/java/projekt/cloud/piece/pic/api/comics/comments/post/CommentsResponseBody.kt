package projekt.cloud.piece.pic.api.comics.comments.post

import kotlinx.serialization.Serializable

@Serializable
class CommentsResponseBody(
    val code: Int,
    val message: String
)