package projekt.cloud.piece.pic

import android.app.Activity
import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import projekt.cloud.piece.pic.api.auth.SignIn
import projekt.cloud.piece.pic.api.base.BaseApiRequest.Companion.request
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
            val signIn = SignIn(account.username, account.password).request()
            if (!signIn.isComplete || !signIn.isErrorResponse || !signIn.isEmptyResponse || !signIn.isRejected()) {
                return@ui returnToLauncher(activity)
            }
            account.signing(signIn.responseBody().token)
            _account.value = account
        }
    }
    
    private fun returnToLauncher(activity: Activity) {
        with(activity) {
            startActivity(LauncherActivity::class.java)
            finish()
        }
    }
    
    private val _systemInsets = MutableLiveData<Rect>()
    val systemInsets: LiveData<Rect>
        get() = _systemInsets
    
    fun obtainSystemInsets(decorView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(decorView) { _, windowInsetsCompat ->
            windowInsetsCompat.getInsets(Type.systemBars()).let {  insets ->
                val rect = Rect()
                rect.top = insets.top
                rect.left = insets.left
                rect.right = insets.right
                rect.bottom = insets.bottom
                _systemInsets.value = rect
            }
            return@setOnApplyWindowInsetsListener windowInsetsCompat
        }
    }

}