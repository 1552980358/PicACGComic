package projekt.cloud.piece.pic.ui.home

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import androidx.annotation.UiThread
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.State
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.search.SearchView.TransitionListener
import com.google.android.material.transition.platform.MaterialElevationScale
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiCategories.CategoriesResponseBody
import projekt.cloud.piece.pic.api.ApiCategories.CategoriesResponseBody.Data.Category
import projekt.cloud.piece.pic.api.ApiCategories.categories
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentHomeBinding
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_ACCOUNT_INVALID
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_NO_ACCOUNT
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.CATEGORIES_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.CATEGORIES_CODE_ERROR_REQUEST
import projekt.cloud.piece.pic.util.CodeBook.CATEGORIES_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.HttpUtil.HTTP_RESPONSE_CODE_SUCCESS
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson
import projekt.cloud.piece.pic.util.StorageUtil.Account

class HomeFragment: BaseFragment<FragmentHomeBinding>(), OnClickListener {

    companion object {
        private const val RECYCLER_VIEW_MAX_SPAN = 2
    }

    class Categories: ViewModel() {
        
        val categories = mutableListOf<Category>()
        val thumbs = mutableMapOf<String, Bitmap?>()
        
        fun requestCategories(token: String, complete: (Int, String?) -> Unit) {
            viewModelScope.ui {
                val httpResponse = withContext(io) {
                    categories(token)
                }
                
                val response = httpResponse.response
                if (httpResponse.code != CATEGORIES_CODE_SUCCESS || response == null) {
                    // Failed from connection
                    return@ui complete.invoke(CATEGORIES_CODE_ERROR_CONNECTION, httpResponse.message)
                }
                if (response.code != HTTP_RESPONSE_CODE_SUCCESS) {
                    // Failed from server
                    return@ui complete.invoke(CATEGORIES_CODE_ERROR_REQUEST, null)
                }
                
                categories.addAll(
                    response.decodeJson<CategoriesResponseBody>()
                        .data
                        .categories
                        .filter { !it.isWeb }
                )
                
                complete.invoke(CATEGORIES_CODE_SUCCESS, null)
            }
            
        }

    }

    private val bottomAppBar: BottomAppBar
        get() = binding.bottomAppBar
    private val floatingActionButton: FloatingActionButton
        get() = binding.floatingActionButton
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    private val searchBar: SearchBar
        get() = binding.searchBar
    private val searchView: SearchView
        get() = binding.searchView

    private lateinit var navController: NavController

    private val categories: Categories by viewModels()
    
    override val snackAnchor: View
        get() = bottomAppBar
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false).apply {
            excludeTarget(R.id.bottom_app_bar, true)
        }
        navController = findNavController()
    }
    
    override fun setViewModels(binding: FragmentHomeBinding) {
        binding.applicationConfigs = applicationConfigs
    }
    
    override fun setUpToolbar() {
        setSupportActionBar(bottomAppBar)
    }
    
    override fun setUpViews() {
        postponeEnterTransition()
    
        val floatingActionButtonMarginBottom = floatingActionButton.marginBottom
        applicationConfigs.windowInsetBottom.observe(viewLifecycleOwner) {
            floatingActionButton.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                updateMargins(bottom = floatingActionButtonMarginBottom + it)
            }
        }
        
        floatingActionButton.setOnClickListener(this)
    
        val recyclerViewAdapter = RecyclerViewAdapter(categories.categories, categories.thumbs) { category, v ->
            if (isAuthSuccess) {
                bottomAppBar.performHide()
                navController.navigate(
                    HomeFragmentDirections.actionHomeToList(category = category.title, listTransition = v.transitionName),
                    FragmentNavigatorExtras(v to v.transitionName)
                )
            }
        }
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), RECYCLER_VIEW_MAX_SPAN)
        recyclerView.doOnPreDraw { startPostponedEnterTransition() }
    
        val spacingOuterHor = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
        val spacingInnerHor = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_4)
        val spacingInnerVer = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
        recyclerView.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
                    super.getItemOffsets(outRect, view, parent, state)
                
                    val pos = parent.getChildLayoutPosition(view)
                    when (pos % RECYCLER_VIEW_MAX_SPAN) {
                        0 -> {
                            outRect.left = spacingOuterHor
                            outRect.right = spacingInnerHor
                        }
                        else -> {
                            outRect.left = spacingInnerHor
                            outRect.right = spacingOuterHor
                        }
                    }
                    val itemCount = recyclerViewAdapter.itemCount
                    val nonFullRow = itemCount % RECYCLER_VIEW_MAX_SPAN
                    when (pos) {
                        in itemCount - (if (nonFullRow != 0) nonFullRow else RECYCLER_VIEW_MAX_SPAN) until itemCount ->
                            applicationConfigs.windowInsetBottom.value?.let { outRect.bottom = it }
                        else -> outRect.bottom = spacingInnerVer
                    }
                }
            }
        )
    
        searchView.editText.addTextChangedListener {
            searchBar.text = it
        }
        
        searchView.editText.setOnEditorActionListener { _, _, _ ->
            searchView.text?.toString()?.let { text ->
                if (text.isNotBlank()) {
                    searchView.addTransitionListener(
                        object: TransitionListener {
                            override fun onStateChanged(searchView: SearchView,
                                                        previousState: SearchView.TransitionState,
                                                        newState: SearchView.TransitionState) {
                                if (newState == SearchView.TransitionState.HIDDEN) {
                                    searchView.removeTransitionListener(this)
                                    navController.navigate(
                                        HomeFragmentDirections.actionHomeToSearch(text),
                                        FragmentNavigatorExtras(searchBar to searchBar.transitionName)
                                    )
                                }
                            }
                        }
                    )
                    searchView.hide()
                }
                return@setOnEditorActionListener true
            }
            false
        }
        
        val resultSearch = getString(R.string.result_search)
        setFragmentResultListener(resultSearch) { _, bundle ->
            if (bundle.containsKey(resultSearch)) {
                searchBar.text = bundle.getString(resultSearch)
            }
        }
        
        lifecycleScope.launchWhenResumed {
            bottomAppBar.performHide(false)
            bottomAppBar.visibility = VISIBLE
            bottomAppBar.performShow()
        }
    }
    
    override fun onBackPressed() = when {
        searchView.isShowing -> {
            searchView.hide()
            false
        }
        else -> super.onBackPressed()
    }

    private fun updateCategories() {
        @Suppress("NotifyDataSetChanged")
        (recyclerView.adapter as RecyclerViewAdapter)
            .notifyDataSetChanged()
    }
    
    @UiThread
    override fun onAuthComplete(code: Int, codeMessage: String?, account: Account?) {
        super.onAuthComplete(code, codeMessage, account)
        val token = account?.token
        if (code != AUTH_CODE_SUCCESS || token == null) {
            // Failed to login
            when (code) {
                AUTH_CODE_ERROR_NO_ACCOUNT -> {
                    sendSnack(getString(R.string.home_snack_login_no_account), resId = R.string.home_snack_action_login) {
                        floatingActionButton.performClick()
                    }
                }
                AUTH_CODE_ERROR_ACCOUNT_INVALID -> {
                    sendSnack(getString(R.string.home_snack_login_invalid_account), resId = R.string.home_snack_action_retry) {
                        applicationConfigs.account.value?.let { requireAuth(it) }
                    }
                }
                AUTH_CODE_ERROR_CONNECTION -> {
                    sendSnack(getString(R.string.home_snack_login_connection_failed, codeMessage), resId = R.string.home_snack_action_retry) {
                        applicationConfigs.account.value?.let { requireAuth(it) }
                    }
                }
            }
            return
        }
        if (categories.categories.isEmpty()) {
            requestCategories(token)
        }
    }
    
    private fun requestCategories(token: String) {
        categories.requestCategories(token) { c, message ->
            when (c) {
                CATEGORIES_CODE_SUCCESS -> updateCategories()
                CATEGORIES_CODE_ERROR_CONNECTION -> {
                    sendSnack(getString(R.string.home_snack_category_connection_failed, message), resId = R.string.home_snack_action_retry) {
                        requestCategories(token)
                    }
                }
                CATEGORIES_CODE_ERROR_REQUEST -> {
                    sendSnack(getString(R.string.home_snack_category_unknown_exception, message), resId = R.string.home_snack_action_retry) {
                        requestCategories(token)
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            floatingActionButton -> {
                bottomAppBar.performHide()
                navController.navigate(
                    HomeFragmentDirections.actionHomeToAccount(),
                    FragmentNavigatorExtras(floatingActionButton to floatingActionButton.transitionName)
                )
            }
        }
    }

}