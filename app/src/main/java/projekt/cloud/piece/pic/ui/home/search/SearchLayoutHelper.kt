package projekt.cloud.piece.pic.ui.home.search

import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.search.SearchBar
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.databinding.FragmentSearchBinding
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode.MEDIUM

object SearchLayoutHelper {

    abstract class SearchLayout(private val binding: FragmentSearchBinding) {

        protected val searchBar: SearchBar
            get() = binding.searchBar

        abstract fun setupSearchBar(fragment: Fragment, navController: NavController)

    }

    private class SearchLayoutCompact(binding: FragmentSearchBinding): SearchLayout(binding) {

        override fun setupSearchBar(fragment: Fragment, navController: NavController) {
            val drawerLayout = fragment.requireParentFragment().requireParentFragment()
                .requireView().findViewById<DrawerLayout>(R.id.drawer_layout)
            fragment.setSupportActionBar(searchBar)
            searchBar.setNavigationOnClickListener {
                if (!drawerLayout.isOpen) {
                    drawerLayout.open()
                }
            }
        }

    }

    private open class SearchLayoutW600dp(binding: FragmentSearchBinding): SearchLayout(binding) {
        override fun setupSearchBar(fragment: Fragment, navController: NavController) {
            fragment.setSupportActionBar(searchBar)
        }
    }

    private class SearchLayoutW1240dp(binding: FragmentSearchBinding): SearchLayoutW600dp(binding) {

    }

    fun FragmentSearchBinding.getSearchLayout(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
        COMPACT -> SearchLayoutCompact(this)
        MEDIUM -> SearchLayoutW600dp(this)
        EXPANDED -> SearchLayoutW1240dp(this)
    }

}