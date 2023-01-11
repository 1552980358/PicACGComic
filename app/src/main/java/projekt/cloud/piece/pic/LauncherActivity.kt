package projekt.cloud.piece.pic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ErrorResponse.checkRejected
import projekt.cloud.piece.pic.api.auth.SignIn.decodeSignInResponse
import projekt.cloud.piece.pic.api.auth.SignIn.signIn
import projekt.cloud.piece.pic.databinding.ActivityLauncherBinding
import projekt.cloud.piece.pic.storage.Account
import projekt.cloud.piece.pic.storage.Account.AccountUtil.getAccount
import projekt.cloud.piece.pic.util.ActivityUtil.startActivity
import projekt.cloud.piece.pic.util.ContextUtil.defaultSharedPreference
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutSizeMode.LayoutSizeModeUtil.getLayoutSize
import projekt.cloud.piece.pic.util.SplashScreenCompat
import projekt.cloud.piece.pic.util.SplashScreenCompat.Companion.applySplashScreenCompat

class LauncherActivity: AppCompatActivity() {
    
    private lateinit var binding: ActivityLauncherBinding

    private open class LauncherLayoutCompat(protected val binding: ActivityLauncherBinding) {
        open fun setupNavigation(navController: NavController) = Unit
    }

    private class LauncherLayoutCompatImpl(
        binding: ActivityLauncherBinding
    ): LauncherLayoutCompat(binding)

    private open class LauncherLayoutCompatW600dpImpl(
        binding: ActivityLauncherBinding
    ): LauncherLayoutCompat(binding) {

        private val navigationView: NavigationView
            get() = binding.navigationView!!

        override fun setupNavigation(navController: NavController) {
            navigationView.setupWithNavController(navController)
        }

    }

    private class LauncherLayoutCompatW1240dpImpl(
        binding: ActivityLauncherBinding
    ): LauncherLayoutCompatW600dpImpl(binding)
    
    private val fragmentContainerView: FragmentContainerView
        get() = binding.fragmentContainerView

    private lateinit var navController: NavController
    
    private lateinit var splashScreenCompat: SplashScreenCompat
    
    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreenCompat = applySplashScreenCompat
        splashScreenCompat.setKeepOnScreen(true)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = fragmentContainerView.getFragment<NavHostFragment>().navController

        val layoutCompat = binding.getLayoutCompat(getLayoutSize())
        layoutCompat.setupNavigation(navController)
        
        lifecycleScope.ui {
            if (!isLaunchDoneBefore()) {
                return@ui startDestinationByDefault()
            }
            val account = getAccount() ?: return@ui startDestinationAtSigning()
            val httpRequest = signIn(account.username, account.password)
            if (!httpRequest.isComplete) {
                return@ui startDestinationAtSigning()
            }
            if (!httpRequest.response.isSuccessful) {
                return@ui startDestinationAtSigning()
            }
            val body = httpRequest.response.body.string()
            if (body.checkRejected()) {
                return@ui startDestinationAtSigning()
            }
            completeAuthSignIn(account.username, account.password, body.decodeSignInResponse().token)
        }
        
    }
    
    private suspend fun isLaunchDoneBefore(): Boolean {
        return withContext(io) {
            defaultSharedPreference.getBoolean(getString(R.string.launcher), false)
        }
    }
    
    private fun ActivityLauncherBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
        COMPACT -> LauncherLayoutCompatImpl(this)
        MEDIUM -> LauncherLayoutCompatW600dpImpl(this)
        EXPANDED -> LauncherLayoutCompatW1240dpImpl(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    
    private fun completeAuthSignIn(username: String, password: String, token: String) {
        completeAuthSignIn(
            bundleOf(
                getString(R.string.auth_sign_in_result_username) to username,
                getString(R.string.auth_sign_in_result_password) to password,
                getString(R.string.auth_sign_in_result_token) to token
            )
        )
    }
    
    private fun completeAuthSignIn(bundle: Bundle) {
        startActivity(MainActivity::class.java, bundle)
        finish()
    }
    
    private fun startDestinationByDefault() {
        setupSignInListener()
        navController.graph = navController.navInflater.inflate(R.navigation.nav_graph_launcher)
        completeSplashScreen()
    }
    
    private fun startDestinationAtSigning() {
        setupSignInListener()
        navController.graph = navController.navInflater.inflate(R.navigation.nav_graph_launcher).apply {
            setStartDestination(R.id.signing)
        }
        completeSplashScreen()
    }
    
    private fun setupSignInListener() {
        fragmentContainerView.getFragment<NavHostFragment>()
            .childFragmentManager
            .setFragmentResultListener(getString(R.string.auth_sign_in_result), this@LauncherActivity) { _, bundle ->
                val username = bundle.getString(getString(R.string.auth_sign_in_result_username))
                val password = bundle.getString(getString(R.string.auth_sign_in_result_password))
                val token = bundle.getString(getString(R.string.auth_sign_in_result_token))
            
                if (!username.isNullOrBlank() && !password.isNullOrBlank() && !token.isNullOrBlank()) {
                    lifecycleScope.io {
                        Account(username, password).save(this@LauncherActivity)
                        defaultSharedPreference.edit {
                            putBoolean(getString(R.string.launcher), true)
                        }
                        ui {
                            // Start to MainActivity
                            completeAuthSignIn(username, password, token)
                        }
                    }
                }
            }
    }
    
    private fun completeSplashScreen() {
        splashScreenCompat.setKeepOnScreen(false)
    }
    
}