package projekt.cloud.piece.pic.ui.signIn

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.platform.MaterialSharedAxis
import kotlinx.coroutines.Job
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.auth.SignIn
import projekt.cloud.piece.pic.api.base.BaseApiRequest.BaseApiRequestUtil.request
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentSignInBinding
import projekt.cloud.piece.pic.ui.signIn.SignInLayoutCompat.SignInLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT

class SignIn: BaseFragment<FragmentSignInBinding>() {

    private var job: Job? = null

    override fun onSetupAnimation(layoutSizeMode: LayoutSizeMode) {
        when (layoutSizeMode) {
            COMPACT -> {
                enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
            }
            else -> {
                enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
            }
        }
    }

    private lateinit var layoutCompat: SignInLayoutCompat

    override fun onSetupLayoutCompat(binding: FragmentSignInBinding, layoutSizeMode: LayoutSizeMode) {
        layoutCompat = binding.getLayoutCompat(layoutSizeMode)
        layoutCompat.setNavController(findNavController())
    }

    override fun onSetupActionBar(binding: FragmentSignInBinding) {
        layoutCompat.setupActionBar(this)
    }

    override fun onSetupView(binding: FragmentSignInBinding) {
        layoutCompat.setCallback { username, password ->
            invokeSignIn(username, password)
        }
        layoutCompat.setupEnterKeyListening()
    }

    private fun invokeSignIn(username: String, password: String) {
        if (job?.isActive != true) {
            job = lifecycleScope.ui {
                val signIn = SignIn(username, password).request()
                
                if (!signIn.isComplete) {
                    layoutCompat.onSignInRequestCompleted()
                    return@ui when (signIn.state) {
                        STATE_IO_EXCEPTION -> {
                            layoutCompat.shortSnack(getString(R.string.request_io_exception, signIn.message))
                        }
                        STATE_EXCEPTION -> {
                            layoutCompat.shortSnack(getString(R.string.request_unknown_exception, signIn.message))
                        }
                        else -> {
                            layoutCompat.shortSnack(getString(R.string.request_unexpected_state, signIn.message))
                        }
                    }
                }
                
                if (signIn.isErrorResponse) {
                    val errorResponseBody = signIn.errorResponse()
                    layoutCompat.onSignInRequestCompleted()
                    return@ui when {
                        signIn.isInvalid(errorResponseBody) -> {
                            layoutCompat.shortSnack(getString(R.string.sign_in_invalid_field))
                        }
                        else -> {
                            layoutCompat.shortSnack(
                                getString(
                                    R.string.response_error,
                                    errorResponseBody.code,
                                    errorResponseBody.error,
                                    errorResponseBody.message,
                                    errorResponseBody.detail
                                )
                            )
                        }
                    }
                }
                
                if (signIn.isEmptyResponse) {
                    layoutCompat.shortSnack(getString(R.string.response_empty))
                }
                
                if (signIn.isRejected()) {
                    layoutCompat.onSignInRequestCompleted()
                    return@ui layoutCompat.shortSnack(getString(R.string.response_rejected))
                }
    
                setFragmentResult(
                    getString(R.string.auth_sign_in_result),
                    bundleOf(
                        getString(R.string.auth_sign_in_result_username) to username,
                        getString(R.string.auth_sign_in_result_password) to password,
                        getString(R.string.auth_sign_in_result_token) to signIn.responseBody().token,
                    )
                )
            }
        }
    }

}