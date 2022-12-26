package projekt.cloud.piece.pic.util

import android.util.Log
import kotlin.Exception
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.GET
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestMethod.POST
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_COMPLETED
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_STARTED
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_WAITING

class HttpRequest private constructor(
    private val domain: String,
    private val path: String,
    private val query: String,
    private val headers: Headers,
    private val method: HttpRequestMethod,
    private val requestBody: RequestBody?
) {

    companion object HttpRequestUtil {

        private var okHttpClient = OkHttpClient.Builder()
            .build()

        private class InvalidStateException: IllegalAccessException("Illegal access: state is not STATE_COMPLETED")

        enum class HttpRequestMethod(val string: String) {
            POST("POST"),
            GET("GET")
        }

        enum class HttpRequestState {
            STATE_WAITING,
            STATE_STARTED,
            STATE_COMPLETED,
            STATE_IO_EXCEPTION,
            STATE_EXCEPTION
        }

        @JvmStatic
        val Map<String, String>.asQueryStr
            get() = when {
                isNotEmpty() -> toList().joinToString(prefix = "?", separator = "&") { it.first + '=' + it.second }
                else -> ""
            }

        private val postJsonBodyType =
            "application/json; charset=UTF-8".toMediaType()

        @JvmStatic
        private fun newRequest(domain: String, path: String, query: String, headers: Headers, method: HttpRequestMethod, requestBody: RequestBody?) =
            HttpRequest(domain, path, query, headers, method, requestBody).request()

        @JvmStatic
        fun getRequest(domain: String, path: String, query: String, headers: Map<String, String> = mapOf()) =
            newRequest(domain, path, query, headers.toHeaders(), GET, null)

        @JvmStatic
        fun postJsonRequest(domain: String, path: String, headers: Map<String, String> = mapOf(), postBody: String) =
            newRequest(domain, path, "", headers.toHeaders(), POST, postBody.toRequestBody(postJsonBodyType))

    }

    var state = STATE_WAITING
        private set

    private var _response: Response? = null
    val response: Response
        get() {
            val response = _response
            if (state != STATE_COMPLETED || response == null) {
                throw InvalidStateException()
            }
            return response
        }

    var exceptionMessage: String? = null
        private set

    fun request() = apply {
        state = STATE_STARTED

        try {
            _response = okHttpClient.newCall(
                Request.Builder()
                    .url(domain + path + query)
                    .headers(headers)
                    .method(method.string, requestBody)
                    .build()
            ).execute()
            state = STATE_COMPLETED
        } catch (e: IOException) {
            handleException(STATE_IO_EXCEPTION, e)
        } catch (e: Exception) {
            handleException(STATE_EXCEPTION, e)
        }
    }

    private fun handleException(newState: HttpRequestState, exception: Exception) {
        exceptionMessage = Log.getStackTraceString(exception)
        state = newState
    }

}