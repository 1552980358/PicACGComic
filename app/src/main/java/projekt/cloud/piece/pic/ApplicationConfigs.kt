package projekt.cloud.piece.pic

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.DisplayUtil.deviceBounds
import projekt.cloud.piece.pic.util.DisplayUtil.getWindowInsets
import projekt.cloud.piece.pic.util.StorageUtil.Account
import projekt.cloud.piece.pic.util.StorageUtil.readAccount

class ApplicationConfigs: ViewModel() {
    
    fun initializeAccount(context: Context, callback: () -> Unit) {
        viewModelScope.ui {
            _account.value = withContext(io) {
                context.readAccount()
            }
            callback.invoke()
        }
    }

    private val _windowInsetTop = MutableLiveData<Int>()
    val windowInsetTop: LiveData<Int>
        get() = _windowInsetTop

    private val _windowInsetBottom = MutableLiveData<Int>()
    val windowInsetBottom: LiveData<Int>
        get() = _windowInsetBottom

    private val _deviceBounds = MutableLiveData<Rect>()
    val deviceBounds: LiveData<Rect>
        get() = _deviceBounds

    fun setUpWindowProperties(view: View) {
        _deviceBounds.value = view.context.deviceBounds
        view.getWindowInsets {
            _windowInsetTop.value = it.top
            _windowInsetBottom.value = it.bottom
        }
    }

    private val _account = MutableLiveData<Account?>()
    val account: LiveData<Account?>
        get() = _account
    fun setAccount(account: Account?) {
        this._account.value = account
    }

    private val _token = MutableLiveData<String?>()
    val token: LiveData<String?>
        get() = _token
    fun updateToken(token: String?) {
        _token.value = token
    }

}