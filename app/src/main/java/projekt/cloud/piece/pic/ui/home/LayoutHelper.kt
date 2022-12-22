package projekt.cloud.piece.pic.ui.home

import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import projekt.cloud.piece.pic.databinding.FragmentHomeBinding
import projekt.cloud.piece.pic.databinding.NavRailHeaderHomeBinding
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode

class HomeLayoutHelper(binding: FragmentHomeBinding, layoutSizeMode: LayoutSizeMode) {

    private open class HomeLayoutCompat(protected val binding: FragmentHomeBinding) {

        protected val navigationView: NavigationView
            get() = binding.navigationView

        open fun setUpNavigationView(navController: NavController) {
            navigationView.setupWithNavController(navController)
        }

    }

    private class HomeLayoutW600dp(binding: FragmentHomeBinding): HomeLayoutCompat(binding) {

        private val drawerLayout: DrawerLayout
            get() = binding.drawerLayout!!
        private val navigationRail: NavigationRailView
            get() = binding.navigationRail!!

        override fun setUpNavigationView(navController: NavController) {
            super.setUpNavigationView(navController)
            navigationRail.setupWithNavController(navController)
            navigationRail.headerView?.let {
                NavRailHeaderHomeBinding.bind(it)
                        .appCompatImageButton
                        .setOnClickListener {
                            if (!drawerLayout.isOpen) {
                                drawerLayout.open()
                            }
                        }
            }
        }

    }

    private class HomeLayoutW1240dp(binding: FragmentHomeBinding) : HomeLayoutCompat(binding) {
    }

    private val homeLayout = when (layoutSizeMode) {
        LayoutSizeMode.COMPACT -> HomeLayoutCompat(binding)
        LayoutSizeMode.MEDIUM -> HomeLayoutW600dp(binding)
        LayoutSizeMode.EXPANDED -> HomeLayoutW1240dp(binding)
    }

    fun setUpNavigationView(navController: NavController) {
        homeLayout.setUpNavigationView(navController)
    }

}