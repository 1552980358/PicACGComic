package projekt.cloud.piece.pic.ui.home.index

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.appbar.MaterialToolbar
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.databinding.FragmentIndexBinding
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED

abstract class IndexLayoutCompat private constructor(protected val binding: FragmentIndexBinding) {
    
    companion object IndexLayoutCompatUtil {
        @JvmStatic
        fun FragmentIndexBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> IndexLayoutCompatImpl(this)
            MEDIUM -> IndexLayoutCompatW600dpImpl(this)
            EXPANDED -> IndexLayoutCompatW1240dpImpl(this)
        }
    }
    
    protected val toolbar: MaterialToolbar
        get() = binding.materialToolbar!!
    
    protected lateinit var navController: NavController
        private set
    
    fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    open fun setupActionBar(fragment: Fragment) = Unit
    
    private class IndexLayoutCompatImpl(binding: FragmentIndexBinding): IndexLayoutCompat(binding) {
    
        override fun setupActionBar(fragment: Fragment) {
            fragment.setSupportActionBar(toolbar)
            // Index -> NavHostFragment -> Home
            val drawerLayout = fragment.requireParentFragment()
                .requireParentFragment()
                .requireView()
                .findViewById<DrawerLayout>(R.id.drawer_layout)
            // Just open drawer, allow NavigationView switching fragments,
            // all fragments in NavController is singleTop mode
            toolbar.setNavigationOnClickListener {
                if (!drawerLayout.isOpen) {
                    drawerLayout.open()
                }
            }
        }
        
    }
    
    private class IndexLayoutCompatW600dpImpl(binding: FragmentIndexBinding): IndexLayoutCompat(binding)
    
    private class IndexLayoutCompatW1240dpImpl(binding: FragmentIndexBinding): IndexLayoutCompat(binding)
    
}