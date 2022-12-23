package projekt.cloud.piece.pic.ui.home.index

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.appbar.MaterialToolbar
import projekt.cloud.piece.pic.R
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
            // Just open drawer, allow NavigationView switching fragments,
            // all fragments in NavController is singleTop mode
            val drawerLayout = fragment.requireParentFragment().requireParentFragment()
                .requireView().findViewById<DrawerLayout>(R.id.drawer_layout)
            toolbar.setNavigationOnClickListener {
                if (!drawerLayout.isOpen) {
                    drawerLayout.open()
                }
            }
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