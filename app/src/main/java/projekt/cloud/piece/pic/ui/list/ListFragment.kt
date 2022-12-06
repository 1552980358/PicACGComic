package projekt.cloud.piece.pic.ui.list

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import androidx.recyclerview.widget.RecyclerView.State
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.Comic
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiComics.ComicsResponseBody
import projekt.cloud.piece.pic.api.ApiComics.ComicsResponseBody.Data.Comics.Doc
import projekt.cloud.piece.pic.api.ApiComics.comics
import projekt.cloud.piece.pic.api.CommonParam.SORT_NEW_TO_OLD
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentListBinding
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.HttpUtil.RESPONSE_CODE_SUCCESS
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson

class ListFragment: BaseFragment<FragmentListBinding>() {

    companion object {

        private const val ARG_CATEGORY = "category"
        private const val ARG_KEYWORD = "keyword"

        private const val GRID_SPAN = 2
        
        private const val REQUEST_FIRST_PAGE = 1
    }

    class Comics: ViewModel() {

        var category: String? = null
        var keyword: String? = null

        private val comicsList = arrayListOf<ComicsResponseBody.Data.Comics>()

        val docs = arrayListOf<Doc>()
        val covers = mutableMapOf<String, Bitmap?>()
        
        private var job: Job? = null
        
        fun initialWithCategory(token: String?, sort: String, success: () -> Unit, failed: (Int) -> Unit) {
            if (token.isNullOrBlank()) {
                return failed.invoke(R.string.list_snack_not_logged_in)
            }
            val category = category
            if (category.isNullOrBlank()) {
                return failed.invoke(R.string.list_snack_keyword_no_blank)
            }
            if (comicsList.isEmpty()) {
                requestCategory(token, category, REQUEST_FIRST_PAGE, sort, success, failed)
            }
        }
        
        fun updateCategoryList(token: String?, sort: String, success: () -> Unit, failed: (Int) -> Unit) {
            if (job?.isActive == true) {
                return
            }
            if (token.isNullOrBlank()) {
                return failed.invoke(R.string.list_snack_not_logged_in)
            }
            val category = category
            if (category.isNullOrBlank()) {
                return failed.invoke(R.string.list_snack_keyword_no_blank)
            }
            if (comicsList.size < comicsList.last().pages) {
                requestCategory(token, category, comicsList.size + 1, sort, success, failed)
            }
        }
        
        private fun requestCategory(token: String, category: String, page: Int, sort: String, success: () -> Unit, failed: (Int) -> Unit) {
            job = viewModelScope.ui {
                val response = withContext(io) {
                    comics(token, page, category, sort)
                } ?: return@ui failed.invoke(R.string.list_snack_exception)
        
                if (response.code != RESPONSE_CODE_SUCCESS) {
                    return@ui failed.invoke(R.string.list_snack_error_code)
                }
        
                val comics = response.decodeJson<ComicsResponseBody>().data.comics
                comicsList.add(comics)
                docs.addAll(comics.docs)
                success.invoke()
                job = null
            }
        }

    }
    
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    private val recyclerView: RecyclerView
        get() = binding.recyclerView

    private var sort = SORT_NEW_TO_OLD

    private val comics: Comics by viewModels()
    private val comic: Comic by activityViewModels()

    private val docs: ArrayList<Doc>
        get() = comics.docs
    private val covers: MutableMap<String, Bitmap?>
        get() = comics.covers

    private val category: String?
        get() = comics.category

    private var requireCaching = false

    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
        sharedElementEnterTransition = MaterialContainerTransform()
    }
    
    override fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentListBinding.inflate(inflater, container, false)
    
    override fun setUpContainerTransitionName() =
        args.getString(getString(R.string.list_transition))
    
    override fun setUpToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController)
    }
    
    override fun setUpViews() {
        postponeEnterTransition()
        
        val category = category
        when {
            !category.isNullOrBlank() -> toolbar.title = category
        }
        val recyclerViewAdapter = RecyclerViewAdapter(docs, covers) { doc, v ->
            requireCaching = true
            comic.setCover(covers[doc._id])
            navController.navigate(
                ListFragmentDirections.actionListToComicDetail(doc._id, v.transitionName),
                FragmentNavigatorExtras(v to v.transitionName)
            )
        }
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = StaggeredGridLayoutManager(GRID_SPAN, VERTICAL)
        recyclerView.doOnPreDraw { startPostponedEnterTransition() }
    
        val spacingInnerHor = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_hor_8)
        val spacingOuterVer = resources.getDimensionPixelSize(R.dimen.md_spec_spacing_ver_8)
        var bottomInset = 0
        applicationConfigs.windowInsetBottom.value?.let {
            bottomInset = it
        }
    
        recyclerView.addItemDecoration(
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
    
        val success = {
            recyclerViewAdapter.notifyListUpdate()
            recyclerView.invalidateItemDecorations()
        }
        val failed: (Int) -> Unit = { resId ->
            navController.navigateUp()
        }
        when {
            args.containsKey(ARG_CATEGORY) -> {
                comics.category = args.getString(ARG_CATEGORY)
                comics.initialWithCategory(applicationConfigs.token.value, sort, success, failed)
            }
            args.containsKey(ARG_KEYWORD) -> {
                comics.keyword = args.getString(ARG_KEYWORD)
            }
            else -> failed.invoke(R.string.list_snack_arg_required)
        }
    
        recyclerView.addOnScrollListener(object: OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1)) {
                    comics.updateCategoryList(applicationConfigs.token.value, sort, success, failed)
                }
            }
        })
    }
    

    override fun onDestroyView() {
        if (!requireCaching) {
            docs.clear()
            covers.clear()
        }
        super.onDestroyView()
    }
    
}