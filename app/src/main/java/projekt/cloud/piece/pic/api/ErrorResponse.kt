package projekt.cloud.piece.pic.api

import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import okhttp3.Response
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.SerializeUtil.decodeJson

object ErrorResponse {

    /**
     * {"code":"","error":"","message":""} or
     * {"code":"","error":"","message":"","detail":""}
     **/
    @Serializable
    data class ErrorResponseBody(val code: Int, val error: Int, val message: String, val detail: String = "")

    suspend fun Response.decodeErrorResponse(): ErrorResponseBody {
        return withContext(io) {
            decodeJson()
        }
    }

    /**
     * {"code":"","message":""}
     **/
    @Serializable
    data class RejectedResponseBody(val code: Int, val message: String)

    suspend fun String.checkRejected(): Boolean {
        return withContext(io) {
            runCatching { decodeJson<RejectedResponseBody>() }
                .getOrNull() != null
        }
    }

}