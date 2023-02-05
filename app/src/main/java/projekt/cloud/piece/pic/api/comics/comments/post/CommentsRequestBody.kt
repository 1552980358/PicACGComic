package projekt.cloud.piece.pic.api.comics.comments.post

import kotlinx.serialization.Serializable

@Serializable
class CommentsRequestBody(
    val content: String
)