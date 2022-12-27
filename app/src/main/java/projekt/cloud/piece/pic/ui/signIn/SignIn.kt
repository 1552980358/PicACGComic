package projekt.cloud.piece.pic.ui.signIn

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.platform.MaterialSharedAxis
import kotlinx.coroutines.Job
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ErrorResponse.checkRejected
import projekt.cloud.piece.pic.api.ErrorResponse.decodeErrorResponse
import projekt.cloud.piece.pic.api.auth.SignIn.SIGN_IN_ERROR_INVALID
import projekt.cloud.piece.pic.api.auth.SignIn.decodeSignInResponse
import projekt.cloud.piece.pic.api.auth.SignIn.signIn
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

    override fun onSetupLayoutHelper(binding: FragmentSignInBinding) {
        layoutCompat = binding.getLayoutCompat(requireActivity())
        layoutCompat.setNavController(findNavController())
    }

    override fun onSetupActionBar(binding: FragmentSignInBinding) {
        layoutCompat.setupActionBar(this)
    }

    override fun onSetupView(binding: FragmentSignInBinding) {
        layoutCompat.setCallback { username, password ->
            invokeSignIn(username, password)
        }
    }

    private fun invokeSignIn(username: String, password: String) {
        if (job?.isActive != true) {
            job = lifecycleScope.ui {
                val httpRequest = signIn(username, password)

                if (httpRequest.isComplete) {
                    val response = httpRequest.response

                    // Error
                    if (!response.isSuccessful) {
                        layoutCompat.onSignInRequestCompleted(requireContext())
                        val error = response.decodeErrorResponse()
                        if (error.error == SIGN_IN_ERROR_INVALID) {
                            return@ui layoutCompat.shortSnack(
                                getString(R.string.sign_in_invalid_field)
                            )
                        }
                        return@ui layoutCompat.shortSnack(
                            getString(
                                R.string.response_error, error.code, error.error, error.message, error.detail
                            )
                        )
                    }

                    val responseText = response.body.string()

                    // Rejected
                    if (responseText.checkRejected()) {
                        layoutCompat.onSignInRequestCompleted(requireContext())
                        return@ui layoutCompat.shortSnack(getString(R.string.response_rejected))
                    }

                    // Complete Auth signing in
                    return@ui setFragmentResult(
                        getString(R.string.auth_sign_in_result),
                        bundleOf(
                            getString(R.string.auth_sign_in_result_username) to username,
                            getString(R.string.auth_sign_in_result_password) to password,
                            getString(R.string.auth_sign_in_result_token) to responseText.decodeSignInResponse().token,
                        )
                    )
                }

                layoutCompat.onSignInRequestCompleted(requireContext())
                when (httpRequest.state) {
                    STATE_IO_EXCEPTION -> {
                        layoutCompat.shortSnack(getString(R.string.request_io_exception, httpRequest.exceptionMessage))
                    }
                    STATE_EXCEPTION -> {
                        layoutCompat.shortSnack(getString(R.string.request_unknown_exception, httpRequest.exceptionMessage))
                    }
                    else -> {
                        layoutCompat.shortSnack(getString(R.string.request_unexpected_state, httpRequest.state.name))
                    }
                }
            }
        }
    }

}