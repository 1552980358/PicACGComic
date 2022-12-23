package projekt.cloud.piece.pic.ui.home.index

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.MaterialToolbar
import projekt.cloud.piece.pic.R.id
import projekt.cloud.piece.pic.R.string
import projekt.cloud.piece.pic.databinding.FragmentIndexBinding
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode.MEDIUM

class IndexLayoutHelper(binding: FragmentIndexBinding, layoutSizeMode: LayoutSizeMode) {

    private open class IndexLayout(protected val binding: FragmentIndexBinding) {

        private val toolbar: MaterialToolbar
            get() = binding.materialToolbar!!

        open fun setupActionBar(fragment: Fragment, navController: NavController) {
            fragment.setSupportActionBar(toolbar)
            toolbar.setupWithNavController(navController)
            val actionBarToggle = ActionBarDrawerToggle(
                fragment.requireActivity(),
                // Index -> NavHostFragment -> Home
                fragment.requireParentFragment().requireParentFragment()
                    .requireView().findViewById(id.drawer_layout),
                binding.materialToolbar,
                string.navigation_drawer_open,
                string.navigation_drawer_close
            )
            actionBarToggle.isDrawerIndicatorEnabled = true
            actionBarToggle.syncState()
        }

    }

    private class IndexLayoutW600dp(binding: FragmentIndexBinding): IndexLayout(binding) {

        override fun setupActionBar(fragment: Fragment, navController: NavController) {
        }

    }

    private class IndexLayoutW1240dp(binding: FragmentIndexBinding): IndexLayout(binding) {

        override fun setupActionBar(fragment: Fragment, navController: NavController) {
        }

    }

    private val indexLayout = when (layoutSizeMode) {
        COMPACT -> IndexLayout(binding)
        MEDIUM -> IndexLayoutW600dp(binding)
        EXPANDED -> IndexLayoutW1240dp(binding)
    }

    fun setupActionBar(fragment: Fragment, navController: NavController) {
        indexLayout.setupActionBar(fragment, navController)
    }

}