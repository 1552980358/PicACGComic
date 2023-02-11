package projekt.cloud.piece.pic.ui.viewer

import android.transition.TransitionManager
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.Bindable
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton.OnChangedCallback
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton.OnVisibilityChangedListener
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.transition.platform.MaterialFadeThrough
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.comics.episode.EpisodeResponseBody.EpisodeImage
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter.BaseRecyclerViewAdapterUtil.adapterInterface
import projekt.cloud.piece.pic.base.SnackLayoutCompat
import projekt.cloud.piece.pic.databinding.FragmentViewerBinding
import projekt.cloud.piece.pic.util.AdapterInterface
import projekt.cloud.piece.pic.util.ContextUtil.defaultSharedPreference
import projekt.cloud.piece.pic.util.ContextUtil.dpToPx
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.ScreenDensity
import projekt.cloud.piece.pic.util.ScreenDensity.COMPACT
import projekt.cloud.piece.pic.util.ScreenDensity.EXPANDED
import projekt.cloud.piece.pic.util.ScreenDensity.MEDIUM

abstract class ViewerLayoutCompat private constructor(
    private val binding: FragmentViewerBinding
): SnackLayoutCompat(), AdapterInterface {
    
    companion object ViewerLayoutCompatUtil {
        @JvmStatic
        fun FragmentViewerBinding.getLayoutCompat(screenDensity: ScreenDensity) = when (screenDensity) {
            COMPACT -> ViewerLayoutCompatImpl(this)
            MEDIUM -> ViewerLayoutCompatW600dpImpl(this)
            EXPANDED -> ViewerLayoutCompatW1240dpImpl(this)
        }
    
        private const val RECYCLER_VIEW_W600dp_DEFAULT_PADDING = 0
        private const val RECYCLER_VIEW_W1240dp_DEFAULT_PADDING = 240
        private const val FIRST_ORDER = 1
        
        private const val RECYCLER_VIEW_MOVE_UP_CHECK_VALUE = -1
        private const val RECYCLER_VIEW_MOVE_DOWN_CHECK_VALUE = 1
    }
    
    private val coordinatorLayout: CoordinatorLayout
        get() = binding.coordinatorLayout
    
    override val snackContainer: View
        get() = coordinatorLayout
    
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    
    private val linearProgressIndicator: LinearProgressIndicator
        get() = binding.linearProgressIndicator
    
    private val next: ExtendedFloatingActionButton
        get() = binding.extendedFloatingActionButtonNext
    
    private val back: ExtendedFloatingActionButton
        get() = binding.extendedFloatingActionButtonBack
    
    private val top: FloatingActionButton
        get() = binding.floatingActionButtonTop
    
    private val fadeThroughDuration =
        coordinatorLayout.resources.getInteger(R.integer.animation_duration).toLong()
    
    @get:Bindable
    var recyclerPadding = 0
        protected set
    
    private val onChangedCallback = object: OnChangedCallback() {
        override fun onHidden(extendedFab: ExtendedFloatingActionButton) {
            extendedFab.isGone = true
        }
    }
    
    private val onVisibilityChangedListener = object: OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton) {
            fab.isGone = true
        }
    }
    
    init {
        @Suppress("LeakingThis")
        binding.layoutCompat = this
        next.hide(onChangedCallback)
        top.hide(onVisibilityChangedListener)
    }
    
    fun setupRecyclerView(fragment: Fragment, order: Int, lastOrder: Int, episodeImageList: List<EpisodeImage>) {
        if (order == FIRST_ORDER) {
            back.hide(onChangedCallback)
        }
        recyclerView.adapter = RecyclerViewAdapter(episodeImageList, fragment)
        val recyclerViewLayoutManager  = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.addOnScrollListener(object: OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val progress = when {
                    !recyclerView.canScrollVertically(RECYCLER_VIEW_MOVE_UP_CHECK_VALUE) -> {
                        if (order != FIRST_ORDER) {
                            if (!back.isShown) {
                                back.show()
                            }
                        }
                        1
                    }
                    !recyclerView.canScrollVertically(RECYCLER_VIEW_MOVE_DOWN_CHECK_VALUE) -> {
                        if (!top.isShown) {
                            top.show()
                        }
                        if (order != lastOrder) {
                            if (!next.isShown) {
                                next.show()
                            }
                        }
                        recyclerViewLayoutManager.itemCount
                    }
                    else -> {
                        if (back.isShown) {
                            back.hide(onChangedCallback)
                        }
                        if (next.isShown) {
                            next.hide(onChangedCallback)
                        }
                        if (top.isShown) {
                            top.hide(onVisibilityChangedListener)
                        }
                        recyclerViewLayoutManager.findLastVisibleItemPosition()
                    }
                }
                
                linearProgressIndicator.setProgressCompat(progress, true)
            }
        })
    }
    
    fun setupActionBar(fragment: Fragment) {
        fragment.setSupportActionBar(toolbar)
    }
    
    fun moveToTop() {
        if (top.isVisible) {
            top.hide(onVisibilityChangedListener)
        }
        if (next.isVisible) {
            next.hide(onChangedCallback)
        }
        // recyclerView.smoothScrollToPosition(0)
        notifyClear()
        notifyUpdate()
    }
    
    fun moveToBack(token: String, viewerViewModel: ViewerViewModel) {
        moveToOrder(token, viewerViewModel.orderValue - 1, viewerViewModel)
    }
    
    fun moveToNext(token: String, viewerViewModel: ViewerViewModel) {
        moveToOrder(token, viewerViewModel.orderValue + 1, viewerViewModel)
    }
    
    private fun moveToOrder(token: String, order: Int, viewerViewModel: ViewerViewModel) {
        notifyClear()
        when (val lifecycleOwner = binding.lifecycleOwner?.lifecycle?.coroutineScope) {
            null -> viewerViewModel.scopedRequestComicImages(token, order)
            else -> viewerViewModel.scopedRequestComicImages(token, order, lifecycleOwner)
        }
    }
    
    override fun notifyUpdate() {
        TransitionManager.beginDelayedTransition(
            coordinatorLayout, MaterialFadeThrough().setDuration(fadeThroughDuration)
        )
        recyclerView.isVisible = true
        recyclerView.adapterInterface.notifyUpdate()
        linearProgressIndicator.max = (recyclerView.layoutManager as LinearLayoutManager).itemCount
    }
    
    override fun notifyClear() {
        TransitionManager.beginDelayedTransition(
            coordinatorLayout, MaterialFadeThrough().setDuration(fadeThroughDuration)
        )
        recyclerView.isVisible = false
        recyclerView.adapterInterface.notifyClear()
    }
    
    private class ViewerLayoutCompatImpl(binding: FragmentViewerBinding): ViewerLayoutCompat(binding)
    
    private class ViewerLayoutCompatW600dpImpl(binding: FragmentViewerBinding): ViewerLayoutCompat(binding) {
        
        init {
            val context = binding.coordinatorLayout.context
            recyclerPadding = context.dpToPx(
                context.defaultSharedPreference.getInt(
                    context.getString(R.string.viewer_padding_w600dp), RECYCLER_VIEW_W600dp_DEFAULT_PADDING
                )
            )
        }
        
    }
    
    private class ViewerLayoutCompatW1240dpImpl(binding: FragmentViewerBinding): ViewerLayoutCompat(binding) {
        
        init {
            val context = binding.coordinatorLayout.context
            recyclerPadding = context.dpToPx(
                context.defaultSharedPreference.getInt(
                    context.getString(R.string.viewer_padding_w1240dp), RECYCLER_VIEW_W1240dp_DEFAULT_PADDING
                )
            )
        }
        
    }
    
}