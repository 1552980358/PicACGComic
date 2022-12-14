package projekt.cloud.piece.pic.ui.read.content

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.Callback
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
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.HttpUtil.HTTP_RESPONSE_CODE_SUCCESS
import projekt.cloud.piece.pic.util.HttpUtil.HttpResponse
import projekt.cloud.piece.pic.util.RecyclerViewUtil.adapterAs
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson
import projekt.cloud.piece.pic.util.StorageUtil.Account

class ComicContentFragment: BaseFragment<FragmentComicContentBinding>(), OnClickListener {

    private val readFragment: ReadFragment
        get() = findParentAs()
    
    private val comic: ComicDetail by activityViewModels()
    
    private val readComic: ReadComic by viewModels(
        ownerProducer = { readFragment }
    )
    
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    private val page: ExtendedFloatingActionButton
        get() = binding.extendedFloatingActionButtonPage
    private val prev: ExtendedFloatingActionButton
        get() = binding.floatingActionButtonPrev
    private val next: ExtendedFloatingActionButton
        get() = binding.floatingActionButtonNext
    
    private lateinit var navController: NavController
    
    private val docs = arrayListOf<Doc>()
    private val images = mutableMapOf<String, Bitmap?>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }
    
    override fun setUpToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController)
    }
    
    override fun setUpViews() {
        recyclerView.adapter = RecyclerViewAdapter(lifecycleScope, docs, images)
    
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateExtendedFabText()
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
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
        
        page.setOnClickListener(this)
        prev.setOnClickListener(this)
        next.setOnClickListener(this)
    }
    
    override fun onAuthComplete(code: Int, codeMessage: String?, account: Account?) {
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
        val comicId = comic.comicId
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
        
        val doc = readComic.doc
        if (doc == null) {
            makeSnack(getString(R.string.comic_content_snack_unknown_episode), LENGTH_SHORT, null, null)
                .addCallback(object: Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        readFragment.findNavController().navigateUp()
                    }
                })
                .show()
            return
        }
        
        lifecycleScope.ui {
            var httpResponse: HttpResponse
            var response: Response?
            
            var page = 0
            
            while (true) {
                httpResponse = withContext(io) {
                    episodeContent(comicId, doc.order, ++page, token)
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
                
                docs.addAll(episode.data.pages.docs)
                
                recyclerView.adapterAs<RecyclerViewAdapter>().notifyListUpdate()
                
                if (episode.data.pages.page == episode.data.pages.pages) {
                    break
                }
            }
            
        }
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
            page -> {}
            prev -> {}
            next -> {}
        }
    }
    
}