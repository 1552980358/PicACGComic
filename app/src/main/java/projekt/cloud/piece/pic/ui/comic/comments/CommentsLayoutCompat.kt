package projekt.cloud.piece.pic.ui.comic.comments

import android.view.View
import android.view.View.GONE
import androidx.core.widget.NestedScrollView
import androidx.core.widget.NestedScrollView.OnScrollChangeListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.LinearProgressIndicator
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter.BaseRecyclerViewAdapterUtil.adapterInterface
import projekt.cloud.piece.pic.base.SnackLayoutCompat
import projekt.cloud.piece.pic.databinding.FragmentCommentsBinding
import projekt.cloud.piece.pic.util.AdapterInterface
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.ViewUtil.canScrollDown

abstract class CommentsLayoutCompat private constructor(
    protected val binding: FragmentCommentsBinding
): SnackLayoutCompat(), AdapterInterface {
    
    companion object CommentsLayoutCompatUtil {
        @JvmStatic
        fun FragmentCommentsBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> CommentsLayoutCompatImpl(this)
            MEDIUM -> CommentsLayoutCompatW600dpImpl(this)
            EXPANDED -> CommentsLayoutCompatW1240dpImpl(this)
        }
    }
    
    override val snackContainer: View
        get() = binding.coordinatorLayout
    
    private val top: RecyclerView
        get() = binding.recyclerViewTop
    private val normal: RecyclerView
        get() = binding.recyclerViewNormal
    
    private val linearProgressIndicator: LinearProgressIndicator
        get() = binding.linearProgressIndicator
    
    open fun setupActionBar(fragment: Fragment) = Unit
    
    open fun setupRecyclerViews(fragment: Fragment, mainViewModel: MainViewModel, commentsViewModel: CommentsViewModel) {
        top.adapter = RecyclerViewAdapter(commentsViewModel.topCommentList, fragment)
        normal.adapter = RecyclerViewAdapter(commentsViewModel.commentList, fragment)
        
        // Set RecyclerView holder pool
        normal.setRecycledViewPool(top.recycledViewPool)
    }
    
    fun setupLoadingIndicator(fragment: Fragment, commentsViewModel: CommentsViewModel) {
        linearProgressIndicator.setVisibilityAfterHide(GONE)
        commentsViewModel.isUpdating.observe(fragment.viewLifecycleOwner) {
            when {
                it -> linearProgressIndicator.show()
                else -> linearProgressIndicator.hide()
            }
        }
    }
    
    override fun notifyClear() = Unit
    
    override fun notifyUpdate() {
        top.adapterInterface.notifyUpdate()
        normal.adapterInterface.notifyUpdate()
    }
    
    private class CommentsLayoutCompatImpl(binding: FragmentCommentsBinding): CommentsLayoutCompat(binding) {
    
        private val toolbar: MaterialToolbar
            get() = binding.materialToolbar!!
        
        private val nestedScrollView: NestedScrollView
            get() = binding.nestedScrollView
        
        override fun setupActionBar(fragment: Fragment) {
            fragment.setSupportActionBar(toolbar)
        }
    
        override fun setupRecyclerViews(fragment: Fragment, mainViewModel: MainViewModel, commentsViewModel: CommentsViewModel) {
            super.setupRecyclerViews(fragment, mainViewModel, commentsViewModel)
            nestedScrollView.setOnScrollChangeListener(OnScrollChangeListener { nestedScrollView, _, _, _, _ ->
                if (!nestedScrollView.canScrollDown && commentsViewModel.hasMorePage) {
                    mainViewModel.account.value?.let { account ->
                        if (account.isSignedIn) {
                            commentsViewModel.scopedObtainCommentsNewPage(account.token, fragment.lifecycleScope)
                        }
                    }
                }
            })
        }
        
    }
    
    private class CommentsLayoutCompatW600dpImpl(binding: FragmentCommentsBinding): CommentsLayoutCompat(binding)
    
    private class CommentsLayoutCompatW1240dpImpl(binding: FragmentCommentsBinding): CommentsLayoutCompat(binding)

}