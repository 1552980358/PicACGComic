package projekt.cloud.piece.pic.ui.splash

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import projekt.cloud.piece.pic.databinding.FragmentSplashBinding
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode.COMPACT

object SplashLayoutHelper {
    
    abstract class SplashLayout(protected val binding: FragmentSplashBinding) {
        
        protected val toolbar: MaterialToolbar
            get() = binding.materialToolbar!!
        
        abstract fun setupToolbar(fragment: Fragment, navController: NavController)
    
    }
    
    private class SplashLayoutCompact(binding: FragmentSplashBinding): SplashLayout(binding) {
        override fun setupToolbar(fragment: Fragment, navController: NavController) {
            fragment.setSupportActionBar(toolbar)
            toolbar.setupWithNavController(navController)
        }
    }
    
    private class SplashLayoutW600dp(binding: FragmentSplashBinding): SplashLayout(binding) {
        override fun setupToolbar(fragment: Fragment, navController: NavController) = Unit
    }
    
    fun FragmentSplashBinding.getSplashLayout(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
        COMPACT -> SplashLayoutCompact(this)
        else -> SplashLayoutW600dp(this)
    }
    
}