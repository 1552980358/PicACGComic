package projekt.cloud.piece.pic.api.common

import kotlinx.serialization.Serializable

@Serializable
class CommentsRequestBody(
    val content: String
)