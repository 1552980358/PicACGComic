package projekt.cloud.piece.pic.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.UiThread
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiAuth
import projekt.cloud.piece.pic.api.ApiAuth.SignInResponseBody
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_ACCOUNT_INVALID
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_NO_ACCOUNT
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.HTTP_REQUEST_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CoroutineUtil
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpUtil
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson
import projekt.cloud.piece.pic.util.StorageUtil.Account

abstract class BaseAuthFragment<VB: ViewBinding>: BaseFragment<VB>() {
    
    private var _token: String? = null
    protected val token: String
        get() = _token!!
    protected var isAuthComplete = false
        private set
    protected val isAuthSuccess: Boolean
        get() = isAuthComplete && _token != null
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applicationConfigs.account.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe onAuthComplete(AUTH_CODE_ERROR_NO_ACCOUNT, null, null)
            }
            requireAuth(it)
        }
    }
    
    protected fun requireAuth(account: Account) {
        lifecycleScope.ui {
            var token = account.token
            if (token != null) {
                _token = token
                return@ui onAuthComplete(AUTH_CODE_SUCCESS, null, account)
            }
            
            val httpResponse = withContext(CoroutineUtil.io) {
                ApiAuth.signIn(account.account, account.password)
            }
            
            // Http code
            val response = httpResponse.response
            if (response == null || httpResponse.code != HTTP_REQUEST_CODE_SUCCESS) {
                return@ui onAuthComplete(AUTH_CODE_ERROR_CONNECTION, httpResponse.message, account)
            }
            // Invalid response code
            if (response.code != HttpUtil.HTTP_RESPONSE_CODE_SUCCESS) {
                return@ui onAuthComplete(AUTH_CODE_ERROR_ACCOUNT_INVALID, null, account)
            }
            
            token = withContext(CoroutineUtil.io) {
                response.decodeJson<SignInResponseBody>().token
            }
            _token = token
            account.token = token
            onAuthComplete(AUTH_CODE_SUCCESS, null, account)
        }
    }
    
    @UiThread
    open fun onAuthComplete(code: Int, codeMessage: String?, account: Account?) {
        Log.i(this::class.java.simpleName, "onAuthComplete")
        isAuthComplete = true
    }
    
}