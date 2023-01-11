package projekt.cloud.piece.pic.ui.viewer

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.LinearProgressIndicator
import projekt.cloud.piece.pic.api.comics.episode.EpisodeResponseBody.EpisodeImage
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter.BaseRecyclerViewAdapterUtil.adapterInterface
import projekt.cloud.piece.pic.base.SnackLayoutCompat
import projekt.cloud.piece.pic.databinding.FragmentViewerBinding
import projekt.cloud.piece.pic.util.AdapterInterface
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM

abstract class ViewerLayoutCompat private constructor(
    private val binding: FragmentViewerBinding
): SnackLayoutCompat(), AdapterInterface {
    
    companion object ViewerLayoutCompatUtil {
        @JvmStatic
        fun FragmentViewerBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> ViewerLayoutCompatImpl(this)
            MEDIUM -> ViewerLayoutCompatW600dpImpl(this)
            EXPANDED -> ViewerLayoutCompatW1240dpImpl(this)
        }
    }
    
    override val snackContainer: View
        get() = binding.coordinatorLayout
    
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    
    private val linearProgressIndicator: LinearProgressIndicator
        get() = binding.linearProgressIndicator
    
    fun setupRecyclerView(fragment: Fragment, episodeImageList: List<EpisodeImage>) {
        recyclerView.adapter = RecyclerViewAdapter(episodeImageList, fragment)
        val recyclerViewLayoutManager  = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object: OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val progress: Int
                when {
                    !recyclerView.canScrollVertically(-1) -> {
                        progress = 1
                    }
                    !recyclerView.canScrollVertically(1) -> {
                        progress = recyclerViewLayoutManager.itemCount
                    }
                    else -> {
                        progress = recyclerViewLayoutManager.findLastVisibleItemPosition()
                    }
                }
                
                linearProgressIndicator.setProgressCompat(progress, true)
            }
        })
    }
    
    fun setupActionBar(fragment: Fragment) {
        fragment.setSupportActionBar(toolbar)
    }
    
    override fun notifyUpdate() {
        recyclerView.adapterInterface.notifyUpdate()
        linearProgressIndicator.max = (recyclerView.layoutManager as LinearLayoutManager).itemCount
    }
    
    override fun notifyClear() {
        recyclerView.adapterInterface.notifyClear()
    }
    
    private class ViewerLayoutCompatImpl(binding: FragmentViewerBinding): ViewerLayoutCompat(binding)
    
    private class ViewerLayoutCompatW600dpImpl(binding: FragmentViewerBinding): ViewerLayoutCompat(binding)
    
    private class ViewerLayoutCompatW1240dpImpl(binding: FragmentViewerBinding): ViewerLayoutCompat(binding)
    
    
}