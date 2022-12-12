package projekt.cloud.piece.pic.ui.account.login.login

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.runBlocking
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.databinding.FragmentLoginBinding
import projekt.cloud.piece.pic.ui.account.base.BaseAccountFragment
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_ACCOUNT_INVALID
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_NO_ACCOUNT
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
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
    
    override fun setViewModels(binding: FragmentLoginBinding) {
        binding.applicationConfigs = applicationConfigs
    }
    
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
            val account = account.editText?.text?.toString()
            val password = password.editText?.text?.toString()
            if (!account.isNullOrBlank() && !password.isNullOrBlank()) {
                requireAuth(Account(account, password))
                linearProgressIndicator.show()
            }
        }
    }

    private val Editable?.isEmail: Boolean
        get() = !this.isNullOrBlank() && this.matches(EMAIL_REGEX)

    private fun checkIfAccountPasswordFilled() {
        login.isEnabled = checkIfFilled(account.editText?.toString(), password.editText?.toString())
    }

    private fun checkIfFilled(account: String?, password: String?) =
        !account.isNullOrBlank() && !password.isNullOrBlank() && password.length >= PASSWORD_MIN_LENGTH
    
    override fun onAuthComplete(code: Int, codeMessage: String?, account: Account?) {
        when (code) {
            AUTH_CODE_SUCCESS -> {
                lifecycleScope.ui {
                    runBlocking(io) { requireContext().saveAccount(account) }
                    applicationConfigs.setAccount(account)
                }
            }
            AUTH_CODE_ERROR_NO_ACCOUNT -> { /** Account not filled **/ }
            AUTH_CODE_ERROR_ACCOUNT_INVALID -> {
                sendSnack(getString(R.string.account_login_snack_auth_invalid))
            }
            AUTH_CODE_ERROR_CONNECTION -> {
                sendSnack(getString(R.string.account_login_snack_auth_connection_failure, codeMessage))
            }
            else -> {
                sendSnack(getString(R.string.account_login_snack_auth_unknown_error, code, codeMessage))
            }
        }
        if (linearProgressIndicator.isVisible) {
            linearProgressIndicator.hide()
        }
    }
    
}