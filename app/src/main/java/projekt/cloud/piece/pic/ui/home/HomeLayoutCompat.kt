package projekt.cloud.piece.pic.ui.home

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.navigationrail.NavigationRailView
import projekt.cloud.piece.pic.databinding.FragmentHomeBinding
import projekt.cloud.piece.pic.databinding.NavRailHeaderHomeBinding
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED

abstract class HomeLayoutCompat(protected val binding: FragmentHomeBinding) {

    companion object HomeLayoutCompatUtil {
        fun FragmentHomeBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> HomeLayoutCompatImpl(this)
            MEDIUM -> HomeLayoutCompatW600dpImpl(this)
            EXPANDED -> HomeLayoutCompatW1240dpImpl(this)
        }
    }

    private val fragmentContainerView: FragmentContainerView
        get() = binding.fragmentContainerView

    protected val drawerLayout: DrawerLayout
        get() = binding.drawerLayout!!
    private val navigationView: NavigationView
        get() = binding.navigationView

    protected val navController = fragmentContainerView.getFragment<NavHostFragment>()
        .navController

    open fun setUpNavigationView() {
        navigationView.setupWithNavController(navController)
    }

    private open class HomeLayoutCompatImpl(binding: FragmentHomeBinding): HomeLayoutCompat(binding)

    private class HomeLayoutCompatW600dpImpl(binding: FragmentHomeBinding): HomeLayoutCompat(binding) {

        private val navigationRail: NavigationRailView
            get() = binding.navigationRail!!

        override fun setUpNavigationView() {
            super.setUpNavigationView()
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

    private class HomeLayoutCompatW1240dpImpl(binding: FragmentHomeBinding): HomeLayoutCompat(binding)

}