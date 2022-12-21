package projekt.cloud.piece.pic.ui.search

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.doOnPreDraw
import androidx.core.widget.addTextChangedListener
import androidx.databinding.ObservableArrayMap
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.State
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.Hold
import com.google.android.material.transition.platform.MaterialContainerTransform
import projekt.cloud.piece.pic.ComicDetail
import projekt.cloud.piece.pic.Comics
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiComics.AdvancedSearchResponseBody.Data.Comics.Doc
import projekt.cloud.piece.pic.api.CommonParam.ComicsSort
import projekt.cloud.piece.pic.api.CommonParam.ComicsSort.MORE_FAVOURITE
import projekt.cloud.piece.pic.api.CommonParam.ComicsSort.MORE_HEART
import projekt.cloud.piece.pic.api.CommonParam.ComicsSort.NEW_TO_OLD
import projekt.cloud.piece.pic.api.CommonParam.ComicsSort.OLD_TO_NEW
import projekt.cloud.piece.pic.base.BaseAuthFragment
import projekt.cloud.piece.pic.databinding.FragmentSearchBinding
import projekt.cloud.piece.pic.util.CodeBook
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_ACCOUNT_INVALID
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_NO_ACCOUNT
import projekt.cloud.piece.pic.util.CodeBook.SEARCH_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.SEARCH_CODE_ERROR_REJECTED
import projekt.cloud.piece.pic.util.CodeBook.SEARCH_CODE_PART_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.SEARCH_CODE_SUCCESS
import projekt.cloud.piece.pic.util.FragmentUtil.addMenuProvider
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.RecyclerViewUtil.adapterAs
import projekt.cloud.piece.pic.util.StorageUtil.Account

class SearchFragment: BaseAuthFragment<FragmentSearchBinding>() {
    
    private companion object {
        private const val GRID_SPAN = 2
    }

    private val comics: Comics by activityViewModels()
    private val comicList: List<Doc>
        get() = comics.searchComicList
    private val coverImages: ObservableArrayMap<String, Bitmap?>
        get() = comics.coverImages
    private val comicDetail: ComicDetail by activityViewModels()
    
    private lateinit var navController: NavController
    
    private val searchBar: SearchBar
        get() = binding.searchBar
    private val searchView: SearchView
        get() = binding.searchView
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        sharedElementEnterTransition = MaterialContainerTransform()
        exitTransition = Hold()
    }
    
    override fun setViewModels(binding: FragmentSearchBinding) {
        binding.applicationConfigs = applicationConfigs
    }
    
    override fun setUpActionBar() {
        setSupportActionBar(searchBar)
        searchBar.setNavigationOnClickListener {
            navController.navigateUp()
        }
        addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                menuInflater.inflate(R.menu.menu_sorting, menu)
                if (menu is MenuBuilder) {
                    @Suppress("RestrictedApi")
                    menu.setOptionalIconsVisible(true)
                }
                when (comics.sort) {
                    NEW_TO_OLD -> {
                        menu.findItem(R.id.menu_sort_new).isChecked = true
                    }
                    OLD_TO_NEW -> {
                        menu.findItem(R.id.menu_sort_old).isChecked = true
                    }
                    MORE_HEART -> {
                        menu.findItem(R.id.menu_sort_heart).isChecked = true
                    }
                    MORE_FAVOURITE -> {
                        menu.findItem(R.id.menu_sort_fav).isChecked = true
                    }
                }
            }
            
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_sort_new -> {
                        updateComicSort(menuItem, NEW_TO_OLD)
                    }
                    R.id.menu_sort_old -> {
                        updateComicSort(menuItem, OLD_TO_NEW)
                    }
                    R.id.menu_sort_heart -> {
                        updateComicSort(menuItem, MORE_HEART)
                    }
                    R.id.menu_sort_fav -> {
                        updateComicSort(menuItem, MORE_FAVOURITE)
                    }
                }
                return true
            }
            
            private fun updateComicSort(menuItem: MenuItem, newSort: ComicsSort) {
                menuItem.isChecked = true
                recyclerView.adapterAs<RecyclerViewAdapter>()
                    .notifyListReset()
                comics.changeSearchSort(token, newSort)
            }
        })
    }
    
    override fun setUpViews() {
        postponeEnterTransition()
        
        searchBar.text = comics.key
        with(searchView.editText) {
            setText(comics.key)
            addTextChangedListener { searchBar.text = it }
            setOnEditorActionListener { _, _, _ ->
                val keyword = text?.toString()
                if (!keyword.isNullOrBlank()) {
                    recyclerView.adapterAs<RecyclerViewAdapter>().notifyListReset()
                    comics.requestSearch(token, keyword)
                    searchView.hide()
                }
                true
            }
        }
        
        with(recyclerView) {
            
            val recyclerViewAdapter = RecyclerViewAdapter(comicList, coverImages) { view, doc ->
                if (isAuthSuccess) {
                    comicDetail.setCover(coverImages[doc.id])
                    comicDetail.requestComic(token, doc.id)
                    navController.navigate(
                        SearchFragmentDirections.toComicDetailFragment(view.transitionName),
                        FragmentNavigatorExtras(view to view.transitionName)
                    )
                }
            }
    
            layoutManager = StaggeredGridLayoutManager(GRID_SPAN, VERTICAL)
            
            adapter = recyclerViewAdapter
            
            addOnScrollListener(object: OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (!recyclerView.canScrollVertically(1) && isAuthSuccess) {
                        comics.requestSearchPage(token)
                    }
                }
            })
    
            val spacingInnerHor = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
            val spacingOuterVer = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
            var bottomInset = 0
            applicationConfigs.windowInsetBottom.value?.let {
                bottomInset = it
            }
            
            addItemDecoration(
                object : ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                        super.getItemOffsets(outRect, view, parent, state)
                        outRect.right = spacingInnerHor
                        val pos = parent.getChildAdapterPosition(view)
                        val itemCount = recyclerViewAdapter.itemCount
                        outRect.bottom = when {
                            itemCount % GRID_SPAN == 0 && pos >= itemCount - GRID_SPAN -> bottomInset
                            pos == itemCount - 1 -> bottomInset
                            else -> spacingOuterVer
                        }
                    }
                }
            )
            
            doOnPreDraw { startPostponedEnterTransition() }
        }
        
        comics.taskReceipt.observe(viewLifecycleOwner) {
            it?.let {
                snackbar?.dismiss()
                snackbar = null
                when (it.code) {
                    SEARCH_CODE_SUCCESS -> { /** List content load success **/ }
                    SEARCH_CODE_PART_SUCCESS -> {
                        recyclerView.adapterAs<RecyclerViewAdapter>().notifyListUpdate()
                        recyclerView.invalidateItemDecorations()
                    }
                    SEARCH_CODE_ERROR_CONNECTION -> {
                        snackbar = sendSnack(getString(R.string.search_snack_login_connection_failed, it.message), resId = R.string.search_snack_request_action_retry) {
                            val category = comics.key
                            comics.clear()
                            comics.requestCategory(token, category)
                        }
                    }
                    SEARCH_CODE_ERROR_REJECTED -> {
                        snackbar = sendSnack(getString(R.string.search_snack_request_server_rejected, it.message), resId = R.string.search_snack_request_action_retry) {
                            val category = comics.key
                            comics.clear()
                            comics.requestCategory(token, category)
                        }
                    }
                    else -> {
                        snackbar = sendSnack(getString(R.string.search_snack_request_unknown_code, it.code, it.message), resId = R.string.search_snack_request_action_retry) {
                            val category = comics.key
                            comics.clear()
                            comics.requestCategory(token, category)
                        }
                    }
                }
            }
        }
    }
    
    override fun onAuthComplete(code: Int, codeMessage: String?, account: Account?) {
        super.onAuthComplete(code, codeMessage, account)
        if (code != CodeBook.AUTH_CODE_SUCCESS || !isAuthSuccess) {
            when (code) {
                AUTH_CODE_ERROR_NO_ACCOUNT -> {
                    sendSnack(getString(R.string.search_snack_login_no_account))
                }
                AUTH_CODE_ERROR_ACCOUNT_INVALID -> {
                    sendSnack(getString(R.string.search_snack_login_invalid_account), resId = R.string.search_snack_request_action_retry) {
                        applicationConfigs.account.value?.let { requireAuth(it) }
                    }
                }
                AUTH_CODE_ERROR_CONNECTION -> {
                    sendSnack(getString(R.string.search_snack_login_connection_failed, codeMessage), resId = R.string.search_snack_request_action_retry) {
                        applicationConfigs.account.value?.let { requireAuth(it) }
                    }
                }
            }
            return
        }
    }
    
    override fun onBackPressed() = when {
        searchView.isShowing -> {
            searchView.hide()
            false
        }
        else -> super.onBackPressed()
    }
    
    override fun onDestroyView() {
        getString(R.string.result_search).let { resultSearch ->
            setFragmentResult(resultSearch, bundleOf(resultSearch to searchBar.text))
        }
        super.onDestroyView()
    }

}