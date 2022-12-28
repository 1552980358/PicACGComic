package projekt.cloud.piece.pic

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import projekt.cloud.piece.pic.api.ErrorResponse.checkRejected
import projekt.cloud.piece.pic.api.auth.SignIn.decodeSignInResponse
import projekt.cloud.piece.pic.api.auth.SignIn.signIn
import projekt.cloud.piece.pic.storage.Account
import projekt.cloud.piece.pic.util.ActivityUtil.startActivity
import projekt.cloud.piece.pic.util.CoroutineUtil.ui

class MainViewModel: ViewModel() {

    private val _account = MutableLiveData<Account>()
    val account: LiveData<Account>
        get() = _account
    
    fun setAccount(username: String, password: String, token: String) {
        var account = account.value
        if (account == null) {
            account = Account(username, password)
            @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
            _account.value = account!!
            if (account.username != username) {
                account.username = username
            }
            if (account.password != password) {
                account.password = password
            }
        }
        account.signing(token)
    }
    
    fun performSignIn(activity: Activity) {
        val account = account.value ?: return returnToLauncher(activity)
        account.signing(null)
        viewModelScope.ui {
            val httpRequest = signIn(account.username, account.password)
            if (!httpRequest.isComplete) {
                return@ui returnToLauncher(activity)
            }
            if (!httpRequest.response.isSuccessful) {
                return@ui returnToLauncher(activity)
            }
            val body = httpRequest.response.body.string()
            if (body.checkRejected()) {
                return@ui returnToLauncher(activity)
            }
            account.signing(body.decodeSignInResponse().token)
            _account.value = account
        }
    }
    
    private fun returnToLauncher(activity: Activity) {
        with(activity) {
            startActivity(LauncherActivity::class.java)
            finish()
        }
    }

}