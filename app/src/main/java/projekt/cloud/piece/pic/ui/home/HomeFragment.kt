package projekt.cloud.piece.pic.ui.home

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.State
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.transition.platform.Hold
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiCategories.CategoriesResponseBody
import projekt.cloud.piece.pic.api.ApiCategories.CategoriesResponseBody.Data.Category
import projekt.cloud.piece.pic.api.ApiCategories.categories
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentHomeBinding
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpUtil.RESPONSE_CODE_SUCCESS
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson
import projekt.cloud.piece.pic.util.SnackUtil.showSnack
import projekt.cloud.piece.pic.util.SnackUtil.snack

class HomeFragment: BaseFragment(), OnClickListener {

    companion object {
        private const val RECYCLER_VIEW_MAX_SPAN = 2
    }

    class Categories: ViewModel() {

        val categories = mutableListOf<Category>()
        val thumbs = mutableMapOf<String, Bitmap?>()
        
        fun updateCategories(token: String?, success: () -> Unit, failed: (Int) -> Unit) {
            if (token == null) {
                if (categories.isNotEmpty()) {
                    categories.clear()
                    thumbs.clear()
                }
                return
            }
            if (categories.isEmpty()) {
                viewModelScope.ui {
                    val response = withContext(io) {
                        categories(token)
                    } ?: return@ui failed.invoke(R.string.home_snack_exception)
                    
                    if (response.code != RESPONSE_CODE_SUCCESS) {
                        return@ui failed.invoke(R.string.home_snack_error_code)
                    }
        
                    categories.addAll(
                        response.decodeJson<CategoriesResponseBody>()
                            .data
                            .categories
                            .filter { !it.isWeb }
                    )
        
                    success.invoke()
                }
            }
        }

    }

    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!
    private val root get() = binding.root
    private val bottomAppBar: BottomAppBar
        get() = binding.bottomAppBar
    private val search: MaterialCardView
        get() = binding.materialCardViewSearch
    private val floatingActionButton: FloatingActionButton
        get() = binding.floatingActionButton
    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    private lateinit var navController: NavController

    private val categories: Categories by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold()
        navController = findNavController()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.applicationConfigs = applicationConfigs
        binding.lifecycleOwner = viewLifecycleOwner

        val floatingActionButtonMarginBottom = floatingActionButton.marginBottom
        applicationConfigs.windowInsetBottom.observe(viewLifecycleOwner) {
            floatingActionButton.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                updateMargins(bottom = floatingActionButtonMarginBottom + it)
            }
        }

        postponeEnterTransition()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).setSupportActionBar(bottomAppBar)
        search.setOnClickListener(this)
        floatingActionButton.setOnClickListener(this)

        val recyclerViewAdapter = RecyclerViewAdapter(categories.categories, categories.thumbs) { category, v ->
            navController.navigate(
                HomeFragmentDirections.actionHomeToList(category = category.title, listTransition = v.transitionName),
                FragmentNavigatorExtras(v to v.transitionName)
            )
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

        applicationConfigs.token.observe(viewLifecycleOwner) {
            if (it == null) {
                root.snack(R.string.home_snack_not_logged_in, LENGTH_INDEFINITE)
                    .setAction(R.string.home_snack_button_login) { floatingActionButton.callOnClick() }
                    .setAnchorView(bottomAppBar)
                    .show()
            }
            categories.updateCategories(
                it,
                success = { updateCategories() },
                failed = { resId ->
                    root.showSnack(resId)
                    updateCategories()
                }
            )
        }
    }

    private fun updateCategories() {
        @Suppress("NotifyDataSetChanged")
        (recyclerView.adapter as RecyclerViewAdapter)
            .notifyDataSetChanged()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onClick(v: View?) {
        when (v) {
            search -> navController.navigate(
                HomeFragmentDirections.actionHomeToSearch(),
                FragmentNavigatorExtras(search to search.transitionName)
            )
            floatingActionButton -> navController.navigate(
                HomeFragmentDirections.actionHomeToAccount(),
                FragmentNavigatorExtras(floatingActionButton to floatingActionButton.transitionName)
            )
        }
    }

}