package projekt.cloud.piece.pic.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import projekt.cloud.piece.pic.R

object StorageUtil {

    @Serializable
    data class Account(val account: String, val password: String) : java.io.Serializable {
        @Transient
        var token: String? = null
    }

    fun Context.saveAccount(account: String, password: String) = Account(account, password).also {
        openFileOutput(getString(R.string.storage_account), MODE_PRIVATE).bufferedWriter()
            .use { bufferedWriter ->
                bufferedWriter.write(Json.encodeToString(it))
                bufferedWriter.flush()
            }
    }

    fun Context.readAccount() = try {
        Json.decodeFromString<Account>(
            openFileInput(getString(R.string.storage_account)).bufferedReader()
                .use { it.readText() }
        )
    } catch (e: Exception) { null }

}