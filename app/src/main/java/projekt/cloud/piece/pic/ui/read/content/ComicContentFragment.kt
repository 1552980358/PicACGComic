package projekt.cloud.piece.pic.ui.read.content

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
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
import kotlinx.coroutines.withContext
import okhttp3.Response
import projekt.cloud.piece.pic.Comic
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiComics
import projekt.cloud.piece.pic.api.ApiComics.EpisodeContentResponseBody.Data
import projekt.cloud.piece.pic.api.ApiComics.EpisodeContentResponseBody.Data.Pages
import projekt.cloud.piece.pic.api.ApiComics.EpisodeContentResponseBody.Data.Pages.Doc
import projekt.cloud.piece.pic.api.ApiComics.episodeContent
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentComicContentBinding
import projekt.cloud.piece.pic.ui.read.ReadComic
import projekt.cloud.piece.pic.ui.read.ReadFragment
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.HttpUtil.RESPONSE_CODE_SUCCESS
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson

class ComicContentFragment: BaseFragment<FragmentComicContentBinding>(), OnClickListener {

    private val readFragment: ReadFragment
        get() = findParentAs()
    
    private val comic: Comic by activityViewModels()
    
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }
    
    override fun setUpToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setupWithNavController(navController)
    }
    
    override fun setUpViews() {
        val pages = arrayListOf<Pages>()
        val images = mutableMapOf<String, Bitmap?>()
        val recyclerViewAdapter = RecyclerViewAdapter(lifecycleScope, docs, images)
        recyclerView.adapter = recyclerViewAdapter
    
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            private fun canExtend(dy: Int) = dy <= 0
        
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateExtendedFabText()
                when {
                    canExtend(dy) -> extendOrShrinkExtendedFab()
                    else -> extendOrShrinkExtendedFab(false)
                }
            }
        
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when {
                    !recyclerView.canScrollVertically(-1) -> {
                        if (page.isVisible) {
                            page.hide()
                        }
                        if (readComic.index > 0 && !prev.isVisible) {
                            prev.show()
                        }
                    }
                    !recyclerView.canScrollVertically(1) -> {
                        if (page.isVisible) {
                            page.hide()
                        }
                        if (readComic.index < comic.docList.lastIndex && !prev.isVisible) {
                            next.show()
                        }
                    }
                    else -> {
                        if (!page.isVisible) {
                            page.show()
                        }
                        if (prev.isVisible) {
                            prev.hide()
                        }
                        if (next.isVisible) {
                            next.hide()
                        }
                    }
                }
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
    
        page.hide()
        if (readComic.index == 0) {
            prev.visibility = GONE
        }
        page.setOnClickListener(this)
        prev.setOnClickListener(this)
        next.setOnClickListener(this)
    
        lifecycleScope.ui {
            val token = applicationConfigs.token.value ?: return@ui failed(R.string.request_not_logged)
            val id = comic.id ?: return@ui failed(R.string.comic_content_snack_arg_required)
        
            toolbar.title = comic.docList[readComic.index].title
            toolbar.subtitle = comic.comic.value?.title
        
            var response: Response?
            var data: Data?
            while (true) {
                response = withContext(io) {
                    episodeContent(id, comic.docList[readComic.index].order, pages.size + 1, token = token)
                } ?: return@ui failed(R.string.comic_content_snack_exception)
                if (response.code != RESPONSE_CODE_SUCCESS) {
                    return@ui failed(R.string.comic_content_snack_error_code)
                }
                data = response.decodeJson<ApiComics.EpisodeContentResponseBody>().data
            
                pages.add(data.pages)
                docs.addAll(data.pages.docs)
            
                succeed()
            
                if (pages.size == data.pages.pages) {
                    break
                }
            }
            updateExtendedFabText()
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
    
    @Synchronized
    private fun extendOrShrinkExtendedFab(extend: Boolean = true) {
        when {
            extend && !page.isExtended -> page.extend()
            !extend && page.isExtended -> page.shrink()
        }
    }
    
    private fun succeed() {
        (recyclerView.adapter as RecyclerViewAdapter).notifyListUpdate()
        recyclerView.invalidate()
    }
    
    private fun failed(@StringRes message: Int) {
        navController.navigateUp()
    }
    
    override fun onClick(v: View) {
        when (v) {
            page -> {}
            prev -> readComic.index--
            next -> readComic.index++
        }
    }
    
}