package projekt.cloud.piece.pic.ui.signing

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.appbar.MaterialToolbar
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.databinding.FragmentSigningBinding
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutSizeMode.LayoutSizeModeUtil.getLayoutSize

abstract class SigningLayoutCompat private constructor(protected val binding: FragmentSigningBinding) {
    
    companion object SigningLayoutCompatUtil {
        @JvmStatic
        fun FragmentSigningBinding.getLayoutCompat(activity: Activity) = when (activity.getLayoutSize()) {
            COMPACT -> SigningLayoutCompatImpl(this)
            MEDIUM -> SigningLayoutCompatW600dpImpl(this)
            EXPANDED -> SigningLayoutCompatW1240dpImpl(this)
        }
    }
    
    protected val toolbar: MaterialToolbar
        get() = binding.materialToolbar!!
    
    protected lateinit var navController: NavController
        private set
    
    init {
        @Suppress("LeakingThis")
        binding.layoutCompat = this
    }
    
    fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    open fun setupActionBar(fragment: Fragment) = Unit
    
    fun onSignInClicked() {
        navController.navigate(SigningDirections.toSignIn())
    }
    
    private class SigningLayoutCompatImpl(binding: FragmentSigningBinding): SigningLayoutCompat(binding) {
    
        override fun setupActionBar(fragment: Fragment) {
            fragment.setSupportActionBar(toolbar)
            toolbar.title = null
            if (navController.graph.startDestinationId != R.id.signing) {
                toolbar.setNavigationIcon(R.drawable.ic_round_arrow_back_24)
                toolbar.setNavigationOnClickListener { navController.navigateUp() }
            }
        }
        
    }
    
    private class SigningLayoutCompatW600dpImpl(binding: FragmentSigningBinding): SigningLayoutCompat(binding)
    
    private class SigningLayoutCompatW1240dpImpl(binding: FragmentSigningBinding): SigningLayoutCompat(binding)
    
}