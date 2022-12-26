package projekt.cloud.piece.pic.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import projekt.cloud.piece.pic.storage.Account.AccountUtil.AccountSignState.SIGNED_IN
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.SerializeUtil.decodeJson
import projekt.cloud.piece.pic.util.SerializeUtil.encodeJson
import projekt.cloud.piece.pic.storage.Account.AccountUtil.AccountSignState.SIGNED_OUT

@Serializable
data class Account(var username: String, var password: String) {

    companion object AccountUtil {

        private class IllegalSignStateException: IllegalStateException("Not signed in")

        private const val ACCOUNT_JSON_FILE_NAME = "account"

        @JvmStatic
        suspend fun Context.getAccount(): Account? {
            return withContext(io) {
                runCatching {
                    openFileInput(ACCOUNT_JSON_FILE_NAME).bufferedReader().use { bufferedReader ->
                        bufferedReader.readText().decodeJson<Account>()
                    }
                }.getOrNull()
            }
        }

        enum class AccountSignState {
            SIGNED_OUT,
            SIGNED_IN
        }

    }

    @Transient
    var state = SIGNED_OUT
        private set

    val isSignedIn: Boolean
        get() = state == SIGNED_IN && _token != null

    @Transient
    private var _token: String? = null
    val token: String
        get() {
            if (!isSignedIn) {
                throw IllegalSignStateException()
            }
            return _token!!
        }

    fun signing(token: String?) {
        state = when (token) {
            null -> SIGNED_IN
            else -> SIGNED_OUT
        }
        _token = token
    }

    fun save(context: Context) {
        context.openFileOutput(ACCOUNT_JSON_FILE_NAME, MODE_PRIVATE).bufferedWriter().use { bufferedWriter ->
            bufferedWriter.write(encodeJson())
        }
    }

    fun saveAsync(context: Context) {
        io {
            context.openFileOutput(ACCOUNT_JSON_FILE_NAME, MODE_PRIVATE).bufferedWriter().use { bufferedWriter ->
                bufferedWriter.write(encodeJson())
            }
        }
    }

}