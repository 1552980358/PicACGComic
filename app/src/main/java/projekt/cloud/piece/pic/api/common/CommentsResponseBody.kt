package projekt.cloud.piece.pic.api.common

import kotlinx.serialization.Serializable

@Serializable
class CommentsResponseBody(
    val code: Int,
    val message: String
)