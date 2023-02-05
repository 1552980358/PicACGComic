package projekt.cloud.piece.pic.ui.comment

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseCallbackFragment
import projekt.cloud.piece.pic.databinding.FragmentCommentBinding
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.COMMENT_COMPLETE
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.COMMENT_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.COMMENT_ERROR
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.COMMENT_EXCEPTION
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.COMMENT_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.COMMENT_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.COMMENT_REJECTED
import projekt.cloud.piece.pic.ui.comment.CommentLayoutCompat.CommentLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.LIKE_COMPLETE
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.LIKE_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.LIKE_ERROR
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.LIKE_EXCEPTION
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.LIKE_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.LIKE_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.comment.CommentViewModel.CommentViewModelUtil.LIKE_REJECTED
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Comment: BaseCallbackFragment<FragmentCommentBinding, CommentViewModel>() {
    
    private companion object {
        const val TAG = "Comment"
    }
    
    private val mainViewModel: MainViewModel by activityViewModels()
    
    private lateinit var layoutCompat: CommentLayoutCompat
    
    override fun onSetupAnimation(layoutSizeMode: LayoutSizeMode) {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }
    
    override fun onBindData(binding: FragmentCommentBinding) {
        binding.mainViewModel = mainViewModel
        binding.commentViewModel = viewModel
        
        requireArguments().getString(getString(R.string.comment_arg_comment))?.let { comment ->
            viewModel.scopedReflectComment(comment, lifecycleScope)
        }
    }
    
    override fun onSetupLayoutCompat(binding: FragmentCommentBinding, layoutSizeMode: LayoutSizeMode) {
        layoutCompat = binding.getLayoutCompat(layoutSizeMode)
    }
    
    override fun onSetupView(binding: FragmentCommentBinding) {
        layoutCompat.setupAvatar(this, viewModel)
        layoutCompat.setupRecyclerView(this, mainViewModel, viewModel)
        
        requireArguments().getString(getString(R.string.comment_arg_id))?.let { id ->
            if (id.isNotBlank()) {
                startRequestReplies(id)
            }
        }
    }
    
    override fun onSetupActionBar(binding: FragmentCommentBinding) {
        layoutCompat.setupActionBar(this)
    }
    
    private fun startRequestReplies(id: String) {
        mainViewModel.account.observe(viewLifecycleOwner) { account ->
            when {
                account.isSignedIn -> {
                    viewModel.scopedObtainCommentReplies(account.token, id, lifecycleScope)
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
            COMMENT_COMPLETE -> {
                layoutCompat.notifyUpdate()
            }
            LIKE_COMPLETE -> {
                layoutCompat.updateCommentLike(this, viewModel, message, responseDetail)
            }
            COMMENT_IO_EXCEPTION, LIKE_IO_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_io_exception, message))
            }
            COMMENT_EXCEPTION, LIKE_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unknown_exception, message))
            }
            COMMENT_ERROR, LIKE_ERROR -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_error, responseCode, code, message, responseDetail))
            }
            COMMENT_EMPTY_CONTENT, LIKE_EMPTY_CONTENT -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_empty))
            }
            COMMENT_REJECTED, LIKE_REJECTED -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_rejected))
            }
            COMMENT_INVALID_STATE_CODE, LIKE_INVALID_STATE_CODE -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unexpected_state, message))
            }
        }
    }

}