package projekt.cloud.piece.pic.ui.signIn

import android.app.Activity
import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.databinding.FragmentSignInBinding
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutSizeMode.LayoutSizeModeUtil.getLayoutSize

typealias SignInButtonOnClickCallback = (String, String) -> Unit

abstract class SignInLayoutCompat(protected val binding: FragmentSignInBinding) {

    companion object SignInLayoutCompatUtil {
        @JvmStatic
        fun FragmentSignInBinding.getLayoutCompat(activity: Activity) = when (activity.getLayoutSize()) {
            COMPACT -> SignInLayoutCompatImpl(this)
            MEDIUM -> SignInLayoutCompatW600dpImpl(this)
            EXPANDED -> SignInLayoutCompatW1240dpImpl(this)
        }
    }

    private val coordinatorLayout: CoordinatorLayout
        get() = binding.coordinatorLayout

    private val username: TextInputLayout
        get() = binding.textInputLayoutUsername
    private val password: TextInputLayout
        get() = binding.textInputLayoutPassword

    private var callback: SignInButtonOnClickCallback? = null
    private var snackBar: Snackbar? = null

    private val signIn: MaterialButton
        get() = binding.materialButtonSignIn

    init {
        @Suppress("LeakingThis")
        binding.layoutCompat = this
    }

    fun setCallback(callback: SignInButtonOnClickCallback) {
        this.callback = callback
    }

    fun shortSnack(message: String) {
        snackBar?.dismiss()
        snackBar = Snackbar.make(coordinatorLayout, message, LENGTH_SHORT)
        snackBar?.show()
    }

    protected lateinit var navController: NavController
        private set

    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    open fun setupActionBar(fragment: Fragment) = Unit

    fun onSignInButtonClick(context: Context) {
        val username = username.editText?.text?.toString()
        val password = password.editText?.text?.toString()
        if (!username.isNullOrBlank() && !password.isNullOrBlank()) {
            context.theme.obtainStyledAttributes(
                intArrayOf(
                    com.google.android.material.R.attr.colorSecondaryContainer,
                    com.google.android.material.R.attr.colorOnSecondaryContainer
                )
            ).use {
                signIn.setBackgroundColor(it.getColor(0, 0))
                @Suppress("ResourceType")
                signIn.setTextColor(it.getColor(1, 0))
            }
            signIn.setText(R.string.sign_in_button_signing_in)
            signIn.icon = IndeterminateDrawable.createCircularDrawable(
                context, CircularProgressIndicatorSpec(context, null)
            )
            callback?.invoke(username, password)
        }
    }

    fun onSignInRequestCompleted(context: Context) {
        signIn.icon = null
        signIn.setText(R.string.sign_in_button)
        context.theme.obtainStyledAttributes(
            intArrayOf(
                com.google.android.material.R.attr.colorPrimary,
                com.google.android.material.R.attr.colorOnPrimary
            )
        ).use {
            signIn.setBackgroundColor(it.getColor(0, 0))
            @Suppress("ResourceType")
            signIn.setTextColor(it.getColor(1, 0))
        }
    }

    private class SignInLayoutCompatImpl(binding: FragmentSignInBinding): SignInLayoutCompat(binding) {

        private val toolbar: MaterialToolbar
            get() = binding.materialToolbar!!

        override fun setupActionBar(fragment: Fragment) {
            fragment.setSupportActionBar(toolbar)
            // toolbar.setupWithNavController(navController)
            toolbar.title = null
            toolbar.setNavigationOnClickListener {
                fragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                navController.navigateUp()
            }
        }

    }

    private class SignInLayoutCompatW600dpImpl(binding: FragmentSignInBinding): SignInLayoutCompat(binding)

    private class SignInLayoutCompatW1240dpImpl(binding: FragmentSignInBinding): SignInLayoutCompat(binding)

}