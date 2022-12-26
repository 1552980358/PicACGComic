package projekt.cloud.piece.pic.ui.home.search

import android.app.Activity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.search.SearchBar
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.databinding.FragmentSearchBinding
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutSizeMode.LayoutSizeModeUtil.getLayoutSize

open class SearchLayoutCompact private constructor(protected val binding: FragmentSearchBinding) {

    companion object SearchLayoutCompactUtil {
        fun FragmentSearchBinding.getLayoutCompat(activity: Activity) = when (activity.getLayoutSize()) {
            COMPACT -> SearchLayoutCompactImpl(this)
            MEDIUM -> SearchLayoutCompactW600dpImpl(this)
            EXPANDED -> SearchLayoutCompactW1240dpImpl(this)
        }
    }

    protected val searchBar: SearchBar
        get() = binding.searchBar

    open fun setupSearchBar(fragment: Fragment) {
        fragment.setSupportActionBar(searchBar)
    }

    private class SearchLayoutCompactImpl(binding: FragmentSearchBinding): SearchLayoutCompact(binding) {

        override fun setupSearchBar(fragment: Fragment) {
            super.setupSearchBar(fragment)
            val drawerLayout = fragment.requireParentFragment().requireParentFragment()
                .requireView().findViewById<DrawerLayout>(R.id.drawer_layout)
            searchBar.setNavigationOnClickListener {
                if (!drawerLayout.isOpen) {
                    drawerLayout.open()
                }
            }
        }

    }

    private class SearchLayoutCompactW600dpImpl(binding: FragmentSearchBinding): SearchLayoutCompact(binding)

    private class SearchLayoutCompactW1240dpImpl(binding: FragmentSearchBinding): SearchLayoutCompact(binding)


}