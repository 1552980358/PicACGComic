package projekt.cloud.piece.pic.ui.list

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.View
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
import androidx.recyclerview.widget.RecyclerView.State
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar.Callback
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.Hold
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.coroutines.withContext
import okhttp3.Response
import projekt.cloud.piece.pic.ComicDetail
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiComics.ComicsResponseBody
import projekt.cloud.piece.pic.api.ApiComics.ComicsResponseBody.Data.Comics.Doc
import projekt.cloud.piece.pic.api.ApiComics.comics
import projekt.cloud.piece.pic.api.CommonBody.ErrorResponseBody
import projekt.cloud.piece.pic.api.CommonParam.SORT_NEW_TO_OLD
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentListBinding
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_ACCOUNT_INVALID
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_NO_ACCOUNT
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.HTTP_REQUEST_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.LIST_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.LIST_CODE_ERROR_REJECTED
import projekt.cloud.piece.pic.util.CodeBook.LIST_CODE_PART_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.LIST_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CompleteCallback
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.HttpUtil.HTTP_RESPONSE_CODE_SUCCESS
import projekt.cloud.piece.pic.util.HttpUtil.HttpResponse
import projekt.cloud.piece.pic.util.RecyclerViewUtil.adapterAs
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson
import projekt.cloud.piece.pic.util.StorageUtil.Account

class ListFragment: BaseFragment<FragmentListBinding>() {

    companion object {
        private const val ARG_CATEGORY = "category"

        private const val GRID_SPAN = 2
    }

    class Comics: ViewModel() {

        var category: String? = null

        val docs = arrayListOf<Doc>()
        val covers = mutableMapOf<String, Bitmap?>()
        
        fun requestCategory(token: String, category: String, sort: String, completeCallback: CompleteCallback) {
            if (docs.isNotEmpty() || covers.isNotEmpty()) {
                docs.clear()
                covers.clear()
            }
            viewModelScope.ui {
                var httpResponse: HttpResponse
                var response: Response?
                var comicsResponseBody: ComicsResponseBody
                var page = 0
                while (true) {
                    httpResponse = withContext(io) {
                        comics(token, ++page, category, sort)
                    }
                    
                    response = httpResponse.response
                    if (httpResponse.code != HTTP_REQUEST_CODE_SUCCESS || response == null) {
                        return@ui completeCallback.invoke(LIST_CODE_ERROR_CONNECTION, httpResponse.message)
                    }
    
                    if (response.code != HTTP_RESPONSE_CODE_SUCCESS) {
                        val errorResponse = withContext(io) {
                            response.decodeJson<ErrorResponseBody>()
                        }
                        return@ui completeCallback.invoke(LIST_CODE_ERROR_REJECTED, errorResponse.message)
                    }
    
                    comicsResponseBody = withContext(io) {
                        response.decodeJson()
                    }
                    
                    val comics = comicsResponseBody.data.comics
                    docs.addAll(comics.docs)
                    
                    completeCallback.invoke(LIST_CODE_PART_SUCCESS, null)
                    
                    if (comics.page == comics.pages) {
                        break
                    }
    
                    completeCallback.invoke(LIST_CODE_SUCCESS, null)
                }
                
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
        exitTransition = Hold()
        comics.category = args.getString(ARG_CATEGORY)
    }
    
    override val containerTransitionName: String?
        get() = args.getString(getString(R.string.list_transition))
    
    override fun setViewModels(binding: FragmentListBinding) {
        binding.comics = comics
    }
    
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
    }
    
    override fun onAuthComplete(code: Int, codeMessage: String?, account: Account?) {
        val token = account?.token
        if (code != AUTH_CODE_SUCCESS || token == null) {
            when (code) {
                AUTH_CODE_ERROR_NO_ACCOUNT -> {
                    sendSnack(getString(R.string.list_snack_login_no_account))
                }
                AUTH_CODE_ERROR_ACCOUNT_INVALID -> {
                    sendSnack(getString(R.string.list_snack_login_invalid_account), resId = R.string.home_snack_action_retry) {
                        applicationConfigs.account.value?.let { requireAuth(it) }
                    }
                }
                AUTH_CODE_ERROR_CONNECTION -> {
                    sendSnack(getString(R.string.list_snack_login_connection_failed, codeMessage), resId = R.string.home_snack_action_retry) {
                        applicationConfigs.account.value?.let { requireAuth(it) }
                    }
                }
            }
            return
        }
        requestComics(token)
    }
    
    private fun requestComics(token: String) {
        val category = category
        if (category == null) {
            makeSnack(getString(R.string.list_snack_category_not_specified), LENGTH_SHORT, null, null).
                addCallback(object: Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        navController.navigateUp()
                    }
                })
            return
        }
        comics.requestCategory(token, category, sort) { code, message ->
            when (code) {
                LIST_CODE_SUCCESS -> { /** List content load success **/ }
                LIST_CODE_PART_SUCCESS -> {
                    recyclerView.adapterAs<RecyclerViewAdapter>().notifyListUpdate()
                }
                LIST_CODE_ERROR_CONNECTION -> {
                    sendSnack(getString(R.string.list_snack_login_connection_failed, message), resId = R.string.list_snack_request_action_retry) {
                        requestComics(token)
                    }
                }
                LIST_CODE_ERROR_REJECTED -> {
                    sendSnack(getString(R.string.list_snack_request_server_rejected, message), resId = R.string.list_snack_request_action_retry) {
                        requestComics(token)
                    }
                }
                else -> {
                    sendSnack(getString(R.string.list_snack_request_unknown_code, code, message), resId = R.string.list_snack_request_action_retry) {
                        requestComics(token)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        if (!requireCaching) {
            docs.clear()
            covers.clear()
        }
        super.onDestroyView()
    }
    
}