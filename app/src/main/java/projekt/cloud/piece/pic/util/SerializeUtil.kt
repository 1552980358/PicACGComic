package projekt.cloud.piece.pic.util

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SerializeUtil {

    inline fun <reified T> String.decodeJson() = Json.decodeFromString<T>(this)

    inline fun <reified T> T.encodeJson() = Json.encodeToString(this)

}