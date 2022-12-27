package projekt.cloud.piece.pic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import projekt.cloud.piece.pic.databinding.ActivityLauncherBinding
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

    private lateinit var navController: NavController
    
    private lateinit var splashScreenCompat: SplashScreenCompat
    
    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreenCompat = applySplashScreenCompat
        splashScreenCompat.setKeepOnScreen(true)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = binding.fragmentContainerView.getFragment<NavHostFragment>().navController

        val layoutCompat = binding.getLayoutCompat(getLayoutSize())
        layoutCompat.setupNavigation(navController)
    }

    private fun ActivityLauncherBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
        COMPACT -> LauncherLayoutCompatImpl(this)
        MEDIUM -> LauncherLayoutCompatW600dpImpl(this)
        EXPANDED -> LauncherLayoutCompatW1240dpImpl(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}