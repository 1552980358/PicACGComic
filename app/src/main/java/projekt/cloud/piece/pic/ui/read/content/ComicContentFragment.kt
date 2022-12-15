package projekt.cloud.piece.pic.ui.read.content

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.databinding.ObservableArrayMap
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.Callback
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import okhttp3.Response
import projekt.cloud.piece.pic.ComicDetail
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiComics.EpisodeContentResponseBody
import projekt.cloud.piece.pic.api.ApiComics.EpisodeContentResponseBody.Data.Pages.Doc
import projekt.cloud.piece.pic.api.ApiComics.episodeContent
import projekt.cloud.piece.pic.api.CommonBody.ErrorResponseBody
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentComicContentBinding
import projekt.cloud.piece.pic.ui.read.ReadComic
import projekt.cloud.piece.pic.ui.read.ReadFragment
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_ACCOUNT_INVALID
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_ERROR_NO_ACCOUNT
import projekt.cloud.piece.pic.util.CodeBook.AUTH_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.HTTP_REQUEST_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.FragmentUtil.setDisplayHomeAsUpEnabled
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.HttpUtil.HTTP_RESPONSE_CODE_SUCCESS
import projekt.cloud.piece.pic.util.HttpUtil.HttpResponse
import projekt.cloud.piece.pic.util.RecyclerViewUtil.adapterAs
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson
import projekt.cloud.piece.pic.util.StorageUtil.Account

class ComicContentFragment: BaseFragment<FragmentComicContentBinding>(), OnClickListener {

    private val readFragment: ReadFragment
        get() = findParentAs()
    
    private val comicDetail: ComicDetail by activityViewModels()
    
    private val readComic: ReadComic by viewModels(
        ownerProducer = { readFragment }
    )
    
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    private val page: ExtendedFloatingActionButton
        get() = binding.extendedFloatingActionButtonPage
    private val prev: FloatingActionButton
        get() = binding.floatingActionButtonPrev
    private val next: FloatingActionButton
        get() = binding.floatingActionButtonNext
    
    private var canMoveToNext = false
    private var canMoveToPrev = false
    
    private lateinit var navController: NavController
    
    private val docs = arrayListOf<Doc>()
    private val images = ObservableArrayMap<String, Bitmap?>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = readFragment.findNavController()
    }
    
    override fun setUpToolbar() {
        setSupportActionBar(toolbar)
        setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { navController.navigateUp() }
    }
    
    override fun setUpViews() {
        binding.title = comicDetail.docList.find { it.order == readComic.order }?.title
        binding.subtitle = comicDetail.comic.value?.title
        
        recyclerView.adapter = RecyclerViewAdapter(docs, images)
        recyclerView.addOnScrollListener(object: OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                when {
                    !recyclerView.canScrollVertically(-1) -> {
                        when {
                            canMoveToPrev -> prev.show()
                            else -> prev.hide()
                        }
                        page.hide()
                        next.hide()
                    }
                    !recyclerView.canScrollVertically(1) -> {
                        prev.hide()
                        page.hide()
                        when {
                            canMoveToNext -> next.show()
                            else -> next.hide()
                        }
                    }
                    else -> {
                        prev.hide()
                        next.hide()
                        if (!page.isExtended) {
                            page.extend()
                        }
                        when {
                            dy < 0 -> page.show()
                            dy > 0 -> page.hide()
                        }
                    }
                }
                updateExtendedFabText()
            }
        })
    
        val extendedFabMarginBottom = page.marginBottom
        applicationConfigs.windowInsetBottom.observe(viewLifecycleOwner) {
            page.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                updateMargins(bottom = it + extendedFabMarginBottom)
            }
            next.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                updateMargins(bottom = it + extendedFabMarginBottom)
            }
            prev.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                updateMargins(bottom = it + extendedFabMarginBottom)
            }
        }
    
        if (readComic.order > 1) {
            canMoveToPrev = true
            prev.setOnClickListener(this)
        }
        if (readComic.order < comicDetail.docList.first().order) {
            canMoveToNext = true
            next.setOnClickListener(this)
        }
    }
    
    override fun onAuthComplete(code: Int, codeMessage: String?, account: Account?) {
        super.onAuthComplete(code, codeMessage, account)
        val token = account?.token
        if (code != AUTH_CODE_SUCCESS || token == null) {
            when (code) {
                AUTH_CODE_ERROR_NO_ACCOUNT -> {
                    makeSnack(getString(R.string.comic_content_snack_auth_no_account), LENGTH_SHORT, null, null)
                        .addCallback(object: Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                readFragment.findNavController().navigateUp()
                            }
                        })
                        .show()
                }
                AUTH_CODE_ERROR_ACCOUNT_INVALID -> {
                    sendSnack(getString(R.string.comic_content_snack_auth_invalid_account),
                        resId = R.string.comic_content_snack_action_retry) {
                        applicationConfigs.account.value?.let { requireAuth(it) }
                    }
                }
                AUTH_CODE_ERROR_CONNECTION -> {
                    sendSnack(getString(R.string.comic_content_snack_auth_connection_failed, codeMessage),
                        resId = R.string.comic_content_snack_action_retry) {
                        applicationConfigs.account.value?.let { requireAuth(it) }
                    }
                }
                else -> {
                    sendSnack(getString(R.string.comic_content_snack_auth_unknown_code, code, codeMessage),
                        resId = R.string.comic_content_snack_action_retry) {
                        applicationConfigs.account.value?.let { requireAuth(it) }
                    }
                }
            }
            return
        }
        requestComicImage(token)
    }
    
    private fun requestComicImage(token: String) {
        val comicId = comicDetail.comicId
        if (comicId.isBlank()) {
            makeSnack(getString(R.string.comic_content_snack_unknown_comic), LENGTH_SHORT, null, null)
                .addCallback(object: Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        readFragment.findNavController().navigateUp()
                    }
                })
                .show()
            return
        }
        
        val order = readComic.order
        var newPageImage: List<Doc>
        
        lifecycleScope.ui {
            var httpResponse: HttpResponse
            var response: Response?
            
            var page = 0
            val jobs = arrayListOf<Job>()
            
            while (true) {
                httpResponse = withContext(io) {
                    episodeContent(comicId, order, ++page, token)
                }
                
                response = httpResponse.response
                if (httpResponse.code != HTTP_REQUEST_CODE_SUCCESS || response == null) {
                    sendSnack(getString(R.string.comic_content_snack_request_connection_failed, httpResponse.message), resId = R.string.comic_content_snack_action_retry) {
                        requestComicImage(token)
                    }
                    return@ui
                }
                
                if (response.code != HTTP_RESPONSE_CODE_SUCCESS) {
                    val errorResponseBody = withContext(io) {
                        response.decodeJson<ErrorResponseBody>()
                    }
                    sendSnack(getString(R.string.comic_content_snack_request_server_rejected, errorResponseBody.message), resId = R.string.comic_content_snack_action_retry) {
                        requestComicImage(token)
                    }
                    return@ui
                }
                
                val episode = withContext(io) {
                    response.decodeJson<EpisodeContentResponseBody>()
                }
    
                newPageImage = episode.data.pages.docs
                docs.addAll(newPageImage)
                jobs.add(
                    io {
                        newPageImage.forEach { doc ->
                            doc.media.bitmap?.let { bitmap -> addImage(doc._id, bitmap) }
                        }
                    }
                )
                recyclerView.adapterAs<RecyclerViewAdapter>().notifyListUpdate()
                
                if (episode.data.pages.page == episode.data.pages.pages) {
                    jobs.forEach {
                        if (it.isActive) {
                            it.join()
                        }
                    }
                    
                    break
                }
            }
            
        }
    }
    
    @Synchronized
    private fun addImage(id: String, bitmap: Bitmap) {
        images[id] = bitmap
    }
    
    private fun updateExtendedFabText() {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val firstPos = layoutManager.findFirstCompletelyVisibleItemPosition()
        val lastPos = layoutManager.findLastCompletelyVisibleItemPosition()
        val pos = when {
            lastPos >= 0 -> lastPos
            firstPos >= 0 -> firstPos
            else -> layoutManager.findFirstVisibleItemPosition()
        }
        @Suppress("SetTextI18n")
        page.text = "${pos + 1} / ${docs.size}"
    }
    
    override fun onClick(v: View) {
        when (v) {
            prev -> {
                if (canMoveToPrev) {
                    readComic.order--
                }
            }
            next -> {
                if (canMoveToNext) {
                    readComic.order++
                }
            }
        }
    }
    
}