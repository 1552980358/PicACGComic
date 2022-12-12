package projekt.cloud.piece.pic.ui.read.content

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.view.View.GONE
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
import projekt.cloud.piece.pic.ComicDetail
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiComics.EpisodeContentResponseBody.Data.Pages
import projekt.cloud.piece.pic.api.ApiComics.EpisodeContentResponseBody.Data.Pages.Doc
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentComicContentBinding
import projekt.cloud.piece.pic.ui.read.ReadComic
import projekt.cloud.piece.pic.ui.read.ReadFragment
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar

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
    
        page.hide()
        if (readComic.index == 0) {
            prev.visibility = GONE
        }
        page.setOnClickListener(this)
        prev.setOnClickListener(this)
        next.setOnClickListener(this)
        
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