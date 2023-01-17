package projekt.cloud.piece.pic.ui.home.search

import android.graphics.Rect
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.Sort
import projekt.cloud.piece.pic.api.Sort.MORE_LIKES
import projekt.cloud.piece.pic.api.Sort.MORE_VIEWS
import projekt.cloud.piece.pic.api.Sort.NEW_TO_OLD
import projekt.cloud.piece.pic.api.Sort.OLD_TO_NEW
import projekt.cloud.piece.pic.api.comics.search.AdvancedSearchResponseBody.Comic
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter.BaseRecyclerViewAdapterUtil.adapterInterface
import projekt.cloud.piece.pic.databinding.FragmentSearchBinding
import projekt.cloud.piece.pic.ui.home.HomeDirections
import projekt.cloud.piece.pic.util.AdapterInterface
import projekt.cloud.piece.pic.util.BitmapBundle.Companion.toNavArg
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED

open class SearchLayoutCompact private constructor(protected val binding: FragmentSearchBinding): AdapterInterface {

    companion object SearchLayoutCompactUtil {
        fun FragmentSearchBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> SearchLayoutCompactImpl(this)
            MEDIUM -> SearchLayoutCompactW600dpImpl(this)
            EXPANDED -> SearchLayoutCompactW1240dpImpl(this)
        }
    }

    protected val searchBar: SearchBar
        get() = binding.searchBar
    
    protected val searchView: SearchView
        get() = binding.searchView
    
    protected val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    private lateinit var navController: NavController
    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    open fun setupSearchBar(fragment: Fragment, mainViewModel: MainViewModel, searchViewModel: SearchViewModel) {
        fragment.setSupportActionBar(searchBar)
        searchView.editText
            .setOnEditorActionListener { textView, _, _ ->
                searchView.hide()
                textView.text?.toString()?.let {  text ->
                    if (text.isNotBlank()) {
                        notifyClear()
                        beginSearch(text, fragment, mainViewModel, searchViewModel)
                    }
                }
                false
            }
        fragment.requireActivity().addMenuProvider(
            object: MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menu.clear()
                    menuInflater.inflate(R.menu.menu_search, menu)
                    
                    menu.findItem(
                        when (searchViewModel.sort) {
                            NEW_TO_OLD -> R.id.sort_new
                            OLD_TO_NEW -> R.id.sort_old
                            MORE_LIKES -> R.id.sort_likes
                            MORE_VIEWS -> R.id.sort_views
                        }
                    ).isChecked = true
                }
    
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    var sort: Sort? = null
                    when (menuItem.itemId) {
                        R.id.sort_new -> {
                            sort = NEW_TO_OLD
                        }
                        R.id.sort_old -> {
                            sort = OLD_TO_NEW
                        }
                        R.id.sort_likes -> {
                            sort = MORE_LIKES
                        }
                        R.id.sort_views -> {
                            sort = MORE_VIEWS
                        }
                    }
                    if (sort != null) {
                        menuItem.isChecked = true
                        mainViewModel.account.value?.let { account ->
                            if (account.isSignedIn) {
                                notifyClear()
                                searchViewModel.updateSort(account.token, sort, fragment.lifecycleScope)
                            }
                        }
                    }
                    return sort == null
                }
            },
            fragment,
            STARTED
        )
    }
    
    private fun beginSearch(text: String, fragment: Fragment, mainViewModel: MainViewModel, searchViewModel: SearchViewModel) {
        mainViewModel.account.value?.let {  account ->
            when {
                account.isSignedIn -> {
                    searchViewModel.scopedAdvancedSearch(
                        account.token, listOf(), text, fragment.lifecycleScope
                    )
                }
                else -> {
                    mainViewModel.performSignIn(fragment.requireActivity())
                }
            }
        }
    }
    
    open fun setupRecyclerView(comicList: List<Comic>, fragment: Fragment) {
        recyclerView.adapter = RecyclerViewAdapter(comicList, fragment) { id, title, appCompatImageView ->
            navController.navigate(
                HomeDirections.toComic(
                    id, title, appCompatImageView.drawable?.toBitmapOrNull()?.toNavArg()
                )
            )
        }
    }
    
    override fun notifyClear() {
        recyclerView.adapterInterface.notifyClear()
    }
    
    override fun notifyUpdate() {
        recyclerView.adapterInterface.notifyUpdate()
    }
    
    private class SearchLayoutCompactImpl(binding: FragmentSearchBinding): SearchLayoutCompact(binding) {

        override fun setupSearchBar(fragment: Fragment, mainViewModel: MainViewModel, searchViewModel: SearchViewModel) {
            super.setupSearchBar(fragment, mainViewModel, searchViewModel)
            val drawerLayout = fragment.requireParentFragment().requireParentFragment()
                .requireView().findViewById<DrawerLayout>(R.id.drawer_layout)
            searchBar.setNavigationOnClickListener {
                if (!drawerLayout.isOpen) {
                    drawerLayout.open()
                }
            }
        }

    }

    private class SearchLayoutCompactW600dpImpl(binding: FragmentSearchBinding): SearchLayoutCompact(binding) {
        
        private companion object {
            const val RECYCLER_SPAN = 2
        }
    
        override fun setupRecyclerView(comicList: List<Comic>, fragment: Fragment) {
            super.setupRecyclerView(comicList, fragment)
            
            (recyclerView.layoutManager as GridLayoutManager).spanCount = RECYCLER_SPAN
            val verticalMargin = fragment.resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            val horizontalMarginInner = fragment.resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
            recyclerView.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        outRect.bottom = verticalMargin
                        if ((view.layoutParams as GridLayoutManager.LayoutParams).spanIndex == 0) {
                            outRect.right = horizontalMarginInner
                        }
                    }
                }
            )
        }
        
    }

    private class SearchLayoutCompactW1240dpImpl(binding: FragmentSearchBinding): SearchLayoutCompact(binding) {
    
        private companion object {
            const val RECYCLER_SPAN = 3
        }
    
        override fun setupRecyclerView(comicList: List<Comic>, fragment: Fragment) {
            super.setupRecyclerView(comicList, fragment)
            (recyclerView.layoutManager as GridLayoutManager).spanCount = RECYCLER_SPAN
            
            val verticalMargin = fragment.resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            val horizontalMarginInner = fragment.resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
            recyclerView.addItemDecoration(
                object: ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        outRect.bottom = verticalMargin
                        if ((view.layoutParams as GridLayoutManager.LayoutParams).spanIndex in (0 .. 1)) {
                            outRect.right = horizontalMarginInner
                        }
                    }
                }
            )
        }
        
    }


}