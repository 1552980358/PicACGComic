package projekt.cloud.piece.pic.util

import android.util.Log
import androidx.annotation.IntRange
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import projekt.cloud.piece.pic.util.CodeBook.HTTP_REQUEST_CODE_EXCEPTION
import projekt.cloud.piece.pic.util.CodeBook.HTTP_REQUEST_CODE_IO_EXCEPTION
import projekt.cloud.piece.pic.util.CodeBook.HTTP_REQUEST_CODE_SUCCESS

object HttpUtil {
    
    class HttpResponse {
        
        @IntRange(from = HTTP_REQUEST_CODE_SUCCESS.toLong(), to = HTTP_REQUEST_CODE_EXCEPTION.toLong())
        var code: Int = HTTP_REQUEST_CODE_SUCCESS
        
        var message: String? = null
        
        var response: Response? = null
        
    }
    
    const val HTTP_RESPONSE_CODE_SUCCESS = 200

    private val okHttpClient = OkHttpClient.Builder()
        .build()

    const val POST = "POST"
    const val GET = "GET"

    val Map<String, Any>.asParams
        get() = when {
            isNotEmpty() -> toList().joinToString(prefix = "?", separator = "&") { it.first + '=' + it.second }
            else -> ""
        }

    private val requestBodyType =
        "application/json; charset=UTF-8".toMediaType()

    fun httpGet(url: String, headers: Map<String, String> = mapOf()) =
        request(url, GET, headers.toHeaders())

    fun httpPost(url: String,
                headers: Map<String, String> = mapOf(),
                requestBody: String) =
        request(url, POST, headers.toHeaders(), requestBody.toRequestBody(requestBodyType))

    private fun request(url: String,
                        method: String,
                        headers: Headers,
                        requestBody: RequestBody? = null): HttpResponse {
        val httpResponse = HttpResponse()
        Log.i("HttpUtil.request", "method=$method url=$url")
        try {
            httpResponse.response = httpRequest(url, method, headers, requestBody)
        } catch (e: IOException) {
            handleException(
                httpResponse,
                method,
                HTTP_REQUEST_CODE_IO_EXCEPTION,
                Log.getStackTraceString(e)
            )
        } catch (e: Exception) {
            handleException(
                httpResponse,
                method,
                HTTP_REQUEST_CODE_EXCEPTION,
                Log.getStackTraceString(e)
            )
        }
        return httpResponse
    }
    
    private fun handleException(httpResponse: HttpResponse, method: String, code: Int, stackTrace: String) {
        httpResponse.code = code
        httpResponse.message = stackTrace
        Log.e("HttpUtil.request.$method", stackTrace)
    }
    
    private fun httpRequest(url: String,
                            method: String,
                            headers: Headers,
                            requestBody: RequestBody?
    ) = okHttpClient.newCall(
            Request.Builder()
                .url(url)
                .headers(headers)
                .method(method, requestBody)
                .build()
        ).execute()

}