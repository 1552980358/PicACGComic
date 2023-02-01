package projekt.cloud.piece.pic.ui.comic.comments

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseCallbackFragment
import projekt.cloud.piece.pic.databinding.FragmentCommentsBinding
import projekt.cloud.piece.pic.ui.comic.Comic
import projekt.cloud.piece.pic.ui.comic.ComicViewModel
import projekt.cloud.piece.pic.ui.comic.comments.CommentsLayoutCompat.CommentsLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.COMMENTS_COMPLETE
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.COMMENTS_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.COMMENTS_ERROR
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.COMMENTS_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.COMMENTS_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.COMMENTS_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.COMMENTS_REJECTED
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.COMMENTS_UPDATE
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.LIKE_COMPLETE
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.LIKE_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.LIKE_ERROR
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.LIKE_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.LIKE_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.LIKE_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.comments.CommentsViewModel.CommentsViewModelUtil.LIKE_REJECTED
import projekt.cloud.piece.pic.util.FragmentUtil.findParentAs
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Comments: BaseCallbackFragment<FragmentCommentsBinding, CommentsViewModel>() {
    
    private companion object {
        const val TAG = "Comments"
    }
    
    private val mainViewModel: MainViewModel by activityViewModels()
    
    private lateinit var layoutCompat: CommentsLayoutCompat
    
    override fun onBindData(binding: FragmentCommentsBinding) {
        binding.mainViewModel = mainViewModel
    }
    
    override fun onSetupLayoutCompat(binding: FragmentCommentsBinding, layoutSizeMode: LayoutSizeMode) {
        layoutCompat = binding.getLayoutCompat(layoutSizeMode)
    }
    
    override fun onSetupActionBar(binding: FragmentCommentsBinding) {
        layoutCompat.setupActionBar(this)
    }
    
    override fun onSetupView(binding: FragmentCommentsBinding) {
        layoutCompat.setupRecyclerViews(this, mainViewModel, viewModel)
        layoutCompat.setupLoadingIndicator(this, viewModel)
        
        val comicViewModel: ComicViewModel by viewModels(
            ownerProducer = { findParentAs<Comic>() }
        )
    
        mainViewModel.account.observe(viewLifecycleOwner) { account ->
            when {
                account.isSignedIn -> {
                    comicViewModel.id.value?.let { id ->
                        viewModel.scopedObtainComments(account.token, id, lifecycleScope)
                    }
                }
                else -> {
                    mainViewModel.performSignIn(requireActivity())
                }
            }
        }
    }
    
    override fun onCallbackReceived(code: Int, message: String?, responseCode: Int?, errorCode: Int?, responseDetail: String?) {
        Log.i(TAG, "onCallbackReceived: code=$code message=$message responseCode=$responseCode errorCode=$errorCode responseDetail=$responseDetail")
        when (code) {
            COMMENTS_UPDATE -> {
                layoutCompat.notifyUpdate()
            }
            COMMENTS_COMPLETE -> {
                // Complete
            }
            LIKE_COMPLETE -> {
                layoutCompat.updateCommentLike(this, viewModel, message, responseDetail)
            }
            COMMENTS_IO_EXCEPTION, LIKE_IO_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_io_exception, message))
            }
            COMMENTS_EXCEPTION, LIKE_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unknown_exception, message))
            }
            COMMENTS_ERROR, LIKE_ERROR -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_error, responseCode, code, message, responseDetail))
            }
            COMMENTS_EMPTY_CONTENT, LIKE_EMPTY_CONTENT -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_empty))
            }
            COMMENTS_REJECTED, LIKE_REJECTED -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_rejected))
            }
            COMMENTS_INVALID_STATE_CODE, LIKE_INVALID_STATE_CODE -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unexpected_state, message))
            }
        }
    }

}