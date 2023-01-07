package projekt.cloud.piece.pic.ui.comic

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.databinding.FragmentComicBinding
import projekt.cloud.piece.pic.databinding.NavHeaderComicBinding
import projekt.cloud.piece.pic.databinding.NavRailHeaderComicBinding
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED

abstract class ComicLayoutCompat(protected val binding: FragmentComicBinding) {
    
    companion object ComicLayoutCompatUtil {
        @JvmStatic
        fun FragmentComicBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> ComicLayoutCompatImpl(this)
            MEDIUM -> ComicLayoutCompatW600dpImpl(this)
            EXPANDED -> ComicLayoutCompatW1240dpImpl(this)
        }
    }
    
    private val fragmentContainerView: FragmentContainerView
        get() = binding.fragmentContainerView
    
    val childNavController: NavController
        get() = fragmentContainerView.getFragment<NavHostFragment>().navController
    
    protected lateinit var navController: NavController
        private set
    
    fun setNavController(navController: NavController) {
        this.navController = navController
    }
    
    abstract fun setupNavigation(fragment: Fragment)
    
    private class ComicLayoutCompatImpl(binding: FragmentComicBinding): ComicLayoutCompat(binding) {
    
        private val bottomNavigationView: BottomNavigationView
            get() = binding.bottomNavigationView!!
        
        override fun setupNavigation(fragment: Fragment) {
            bottomNavigationView.setupWithNavController(childNavController)
        }
        
    }
    
    private class ComicLayoutCompatW600dpImpl(binding: FragmentComicBinding): ComicLayoutCompat(binding) {
    
        private val drawerLayout: DrawerLayout
            get() = binding.drawerLayout!!
        private val navigationView: NavigationView
            get() = binding.navigationView!!
        private val navigationRailView: NavigationRailView
            get() = binding.navigationRailView!!
    
        override fun setupNavigation(fragment: Fragment) {
            navigationView.setupWithNavController(childNavController)
            navigationRailView.setupWithNavController(childNavController)
            navigationRailView.headerView?.let { headerView ->
                with(NavRailHeaderComicBinding.bind(headerView)) {
                    appCompatImageButton.setOnClickListener {
                        if (!drawerLayout.isOpen) {
                            drawerLayout.open()
                        }
                    }
                }
            }
        }
        
    }
    
    private class ComicLayoutCompatW1240dpImpl(binding: FragmentComicBinding): ComicLayoutCompat(binding) {
    
        private val navigationView: NavigationView
            get() = binding.navigationView!!
        
        override fun setupNavigation(fragment: Fragment) {
            navigationView.setupWithNavController(childNavController)
            with(NavHeaderComicBinding.bind(navigationView.getHeaderView(0))) {
                appCompatImageButton.setOnClickListener {
                    fragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                    navController.navigateUp()
                }
            }
        }
        
    }
    
}