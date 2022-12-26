package projekt.cloud.piece.pic.api

import com.fasterxml.uuid.Generators
import com.fasterxml.uuid.UUIDTimer
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod
import java.util.Random
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

class Header private constructor() {

    companion object HeaderUtil {

        private const val ALGORITHM_SHA256 = "HmacSHA256"
        private val Byte.hex get() = String.format("%02x", this)

        private const val API_KEY = "C69BAF41DA5ABD1FFEDC6D2FEA56B"
        private const val PRIVATE_KEY = "~d}\$Q7\$eIni=V)9\\RK/P.RM4;9[7|@/CA}b~OW!3?EV`:<>M7pddUBL5n|0/*Cn"

        private var _header = Header()

        @JvmStatic
        fun getHeader(path: String, query: String = "", requestMethod: HttpRequestMethod, token: String = ""): Map<String, String> {
            _header.updateMetadata(path, query, requestMethod)
            return _header.headers(token)
        }

    }

    private lateinit var path: String
    private lateinit var query: String
    private lateinit var requestMethod: HttpRequestMethod
    private var token = ""

    fun updateMetadata(path: String, query: String, requestMethod: HttpRequestMethod) {
        this.path = path
        this.query = query
        this.requestMethod = requestMethod
    }

    private fun headers(token: String): Map<String, String> {
        val time = System.currentTimeMillis()
        val nonce = Generators.timeBasedGenerator(null, UUIDTimer(Random(time), null))
            .generate()
            .toString()
            .replace("-", "")
        val timeStr = (time / 1000).toString()
        return when {
            token.isEmpty() -> headerWithoutAuth(timeStr, nonce)
            else -> headerWithAuth(timeStr, nonce)
        }
    }

    private fun headerWithAuth(timeStr: String, nonce: String) = mapOf(
        "api-key" to API_KEY,
        "accept" to "application/vnd.picacomic.com.v1+json",
        "User-Agent" to "okhttp/3.8.1",
        "app-platform" to "android",
        "app-uuid" to "defaultUuid",
        "app-build-version" to "2.2.1.3.3.4",
        "app-channel" to "3",
        "image-quality" to "original",
        "app-build-version" to "45",
        "time" to timeStr,
        "nonce" to nonce,
        "signature" to generateSignature(
            PRIVATE_KEY.toByteArray(),
            processData(path, query, timeStr, nonce, requestMethod)
        ),
        "authorization" to token
    )

    private fun headerWithoutAuth(timeStr: String, nonce: String) = mapOf(
        "api-key" to API_KEY,
        "accept" to "application/vnd.picacomic.com.v1+json",
        "User-Agent" to "okhttp/3.8.1",
        "app-platform" to "android",
        "app-uuid" to "defaultUuid",
        "app-build-version" to "2.2.1.3.3.4",
        "app-channel" to "3",
        "image-quality" to "original",
        "app-build-version" to "45",
        "time" to timeStr,
        "nonce" to nonce,
        "signature" to generateSignature(
            PRIVATE_KEY.toByteArray(),
            processData(path, query, timeStr, nonce, requestMethod)
        )
    )

    private fun generateSignature(key: ByteArray, data: ByteArray) =
        Mac.getInstance(ALGORITHM_SHA256)
            .apply { init(SecretKeySpec(key, ALGORITHM_SHA256)) }
            .doFinal(data)
            .joinToString("") { it.hex }

    private fun processData(path: String, query: String, time: String, nonce: String, requestMethod: HttpRequestMethod) =
        (path + query + time + nonce + requestMethod.string + API_KEY).lowercase().toByteArray()

}