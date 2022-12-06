package projekt.cloud.piece.pic.ui.account.login.login

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.platform.MaterialFadeThrough
import kotlinx.coroutines.Job
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiAuth
import projekt.cloud.piece.pic.api.ApiAuth.signIn
import projekt.cloud.piece.pic.databinding.FragmentLoginBinding
import projekt.cloud.piece.pic.ui.account.base.BaseAccountFragment
import projekt.cloud.piece.pic.ui.account.detail.AccountDetailFragment
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpUtil.RESPONSE_CODE_BAD_REQUEST
import projekt.cloud.piece.pic.util.HttpUtil.RESPONSE_CODE_SUCCESS
import projekt.cloud.piece.pic.util.StorageUtil.Account
import projekt.cloud.piece.pic.util.StorageUtil.saveAccount

class LoginFragment: BaseAccountFragment<FragmentLoginBinding>() {

    private companion object {
        val EMAIL_REGEX = "([0-9a-zA-Z]+.)*[0-9a-zA-Z]+@([0-9a-zA-Z].)+.([0-9a-zA-Z])+".toRegex()
        val ACCOUNT_REGEX = "[0-9a-zA-Z._@]+".toRegex()
        const val ACCOUNT_MAX_LENGTH = 32
        
        val PASSWORD_REGEX = "[0-9a-zA-Z._]+".toRegex()
        const val PASSWORD_MIN_LENGTH = 8
        const val PASSWORD_MAX_LENGTH = 32
        
        const val EMPTY_STR = ""
    }

    private val linearProgressIndicator: LinearProgressIndicator
        get() = binding.linearProgressIndicator
    private val account: TextInputLayout
        get() = binding.textInputLayoutAccount
    private val password: TextInputLayout
        get() = binding.textInputLayoutPassword
    private val login: MaterialButton
        get() = binding.materialButtonLogin

    private var job: Job? = null
    
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLoginBinding.inflate(inflater, container, false)
    
    override fun setViewModels(binding: FragmentLoginBinding) {
        binding.applicationConfigs = applicationConfigs
        binding.lifecycleOwner = viewLifecycleOwner
    }
    
    override fun setUpContainerTransitionName(): String? = null
    
    override fun setUpViews() {
        var accountIcon = R.drawable.ic_round_account_circle_24
        account.editText?.let { editText ->
            editText.filters = arrayOf(
                InputFilter { source, _, _, _, _, _ ->
                    when {
                        source.matches(ACCOUNT_REGEX) -> source
                        else -> EMPTY_STR
                    }
                },
                InputFilter.LengthFilter(ACCOUNT_MAX_LENGTH)
            )
            editText.addTextChangedListener {
                val drawableId = when {
                    it.isEmail -> R.drawable.ic_round_email_24
                    else -> R.drawable.ic_round_account_circle_24
                }
                if (accountIcon != drawableId) {
                    accountIcon = drawableId
                    account.setStartIconDrawable(accountIcon)
                }
                checkIfAccountPasswordFilled()
            }
        }
    
        with(password) {
            editText?.let { editText ->
                editText.inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
                editText.addTextChangedListener {
                    checkIfAccountPasswordFilled()
                }
            
                editText.filters = arrayOf(
                    InputFilter { source, _, _, _, _, _ ->
                        when {
                            source.matches(PASSWORD_REGEX) -> source
                            else -> EMPTY_STR
                        }
                    },
                    InputFilter.LengthFilter(PASSWORD_MAX_LENGTH)
                )
            
                var isPasswordVisible = false
                setEndIconOnClickListener {
                    isPasswordVisible = !isPasswordVisible
                
                    val currentCursor = editText.selectionEnd
                    val inputType: Int
                    @DrawableRes val icon: Int
                    when {
                        isPasswordVisible -> {
                            inputType = TYPE_CLASS_TEXT
                            icon = R.drawable.ic_round_visibility_off_24
                        }
                        else -> {
                            inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
                            icon = R.drawable.ic_round_visibility_24
                        }
                    }
                    editText.inputType = inputType
                    editText.setSelection(currentCursor)
                
                    password.setEndIconDrawable(icon)
                }
            }
        
        }
    
        linearProgressIndicator.hide()
        login.setOnClickListener {
            (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view?.windowToken, 0)
            if (job != null) {
                return@setOnClickListener
            }
            job = account.editText?.text?.toString()?.let { account ->
                password.editText?.text?.toString()?.let { password ->
                    getAuth(account, password)
                }
            }
            linearProgressIndicator.show()
        }
    }

    private val Editable?.isEmail: Boolean
        get() = !this.isNullOrBlank() && this.matches(EMAIL_REGEX)

    private fun checkIfAccountPasswordFilled() {
        login.isEnabled = checkIfFilled(account.editText?.toString(), password.editText?.toString())
    }

    private fun checkIfFilled(account: String?, password: String?) =
        !account.isNullOrBlank() && !password.isNullOrBlank() && password.length >= PASSWORD_MIN_LENGTH

    private fun getAuth(account: String, password: String) = io {
        when (val response = signIn(account, password)) {
            null -> authError(R.string.account_login_snack_auth_exception)
            else -> when (response.code) {
                RESPONSE_CODE_SUCCESS -> {
                    authSuccess(
                        Json.decodeFromString<ApiAuth.SignInResponseBody>(response.body.string()).token,
                        requireContext().saveAccount(account, password)
                    )
                }
                RESPONSE_CODE_BAD_REQUEST -> authError(R.string.account_login_snack_auth_invalid)
                else -> authError(getString(R.string.account_login_snack_auth_error_code) + response.code)
            }
        }
        job = null
    }

    private fun authError(@StringRes resId: Int) = authCompleteTask(getString(resId))

    private fun authError(message: String) = authCompleteTask(message)

    private fun authSuccess(token: String, account: Account) = authCompleteTask(null) {
        with(applicationConfigs) {
            updateToken(token)
            setAccount(account)
        }

        requireParentFragment().exitTransition = MaterialFadeThrough()
        transactionTo(AccountDetailFragment(), MaterialFadeThrough())
    }

    private fun authCompleteTask(message: String?, block: (() -> Unit)? = null) = ui {
        message?.let { /*root.showSnack(it)*/ }
        linearProgressIndicator.hide()
        block?.invoke()
    }
    
}