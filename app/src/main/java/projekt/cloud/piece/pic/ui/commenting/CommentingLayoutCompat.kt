package projekt.cloud.piece.pic.ui.commenting

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.net.URLEncoder
import java.nio.charset.Charset
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.base.BaseApiRequest.BaseApiRequestUtil.request
import projekt.cloud.piece.pic.api.base.BaseStringApiRequest
import projekt.cloud.piece.pic.api.common.CommentsResponseBody
import projekt.cloud.piece.pic.base.SnackLayoutCompat
import projekt.cloud.piece.pic.databinding.DialogFragmentCommentingBinding
import projekt.cloud.piece.pic.util.CoroutineUtil.default
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.widget.DefaultedImageView

typealias ComicComment = projekt.cloud.piece.pic.api.comics.comments.post.Comments
typealias ReplyComment = projekt.cloud.piece.pic.api.comments.Comments

abstract class CommentingLayoutCompat private constructor(
    protected val binding: DialogFragmentCommentingBinding
): SnackLayoutCompat() {

    companion object CommentingLayoutCompatUtil {
        @JvmStatic
        fun DialogFragmentCommentingBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> CommentingLayoutCompatImpl(this)
            MEDIUM -> CommentingLayoutCompatW600dpImpl(this)
            EXPANDED -> CommentingLayoutCompatW1240dpImpl(this)
        }
    }
    
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    
    private val linearProgressIndicator: LinearProgressIndicator
        get() = binding.linearProgressIndicator
    
    private val textInputLayout: TextInputLayout
        get() = binding.textInputLayout
    private val textInputEditText: TextInputEditText
        get() = textInputLayout.editText as TextInputEditText
    
    private val defaultedImageViewAvatar: DefaultedImageView
        get() = binding.defaultedImageViewAvatar
    
    init {
        linearProgressIndicator.setVisibilityAfterHide(GONE)
    }
    
    override val snackContainer: View
        get() = binding.root
    
    fun setupActionBar(fragment: Fragment) {
        fragment.setSupportActionBar(toolbar)
    }
    
    fun setupWithArgument(fragment: Fragment, arguments: Bundle) {
        fragment.lifecycleScope.ui {
            binding.comicId = withContext(default) {
                arguments.getString(fragment.getString(R.string.commenting_arg_comic_id))
            }
            binding.title = withContext(default) {
                arguments.getString(fragment.getString(R.string.commenting_arg_comic_title))
            }
            binding.creator = withContext(default) {
                arguments.getString(fragment.getString(R.string.commenting_arg_comic_creator))
            }
            binding.comment = withContext(default) {
                arguments.getString(fragment.getString(R.string.commenting_arg_comment_comment))
            }
            binding.commentId = withContext(default) {
                arguments.getString(fragment.getString(R.string.commenting_arg_comment_id))
            }
            binding.user = withContext(default) {
                arguments.getString(fragment.getString(R.string.commenting_arg_comment_user))
            }
            setupUserAvatar(
                withContext(default) {
                    arguments.getString(fragment.getString(R.string.commenting_arg_comment_user_avatar))
                },
                fragment
            )
        }
    }
    
    private fun setupUserAvatar(avatar: String?, fragment: Fragment) {
        if (avatar.isNullOrBlank()) {
            return defaultedImageViewAvatar.switchToDefault()
        }
        fragment.lifecycleScope.ui {
            Glide.with(fragment)
                .load(avatar)
                .placeholder(defaultedImageViewAvatar.defaultDrawable)
                .listener(
                    object: RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            defaultedImageViewAvatar.switchToDefault()
                            return false
                        }
                        override fun onResourceReady(
                            resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean
                        ) = false
                    }
                )
                .into(defaultedImageViewAvatar)
        }
    }
    
    fun setupMenu(dialogFragment: DialogFragment) {
        dialogFragment.requireActivity().addMenuProvider(
            object: MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menu.clear()
                    menuInflater.inflate(R.menu.menu_commenting, menu)
                }
                override fun onMenuItemSelected(menuItem: MenuItem) =
                    when (menuItem.itemId) {
                        R.id.send -> {
                            hideKeyboard(dialogFragment.requireActivity())
                            sendComment(
                                dialogFragment,
                                dialogFragment.activityViewModels<MainViewModel>().value
                            )
                            true
                        }
                        else -> false
                    }
            },
            dialogFragment.viewLifecycleOwner,
            STARTED
        )
    }
    
    private fun hideKeyboard(activity: Activity) {
        (activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binding.root.rootView.windowToken, 0)
    }
    
    private fun sendComment(dialogFragment: DialogFragment, mainViewModel: MainViewModel) {
        mainViewModel.account.value
            ?.takeIf { it.isSignedIn }
            ?.let { account ->
                dialogFragment.lifecycleScope.ui {
                    textInputEditText.text
                        ?.toString()
                        ?.takeIf { it.isNotBlank() }
                        ?.let { comment ->
                            postComment(
                                dialogFragment,
                                account.token,
                                withContext(default) {
                                    URLEncoder.encode(comment, Charset.defaultCharset().displayName())
                                }
                            )
                        }
                }
            }
    }
    
    private suspend fun postComment(dialogFragment: DialogFragment, token: String, comment: String) {
        if (linearProgressIndicator.isGone) {
            linearProgressIndicator.show()
        }
        val comicId = binding.comicId
        val commentId = binding.commentId
        when {
            !comicId.isNullOrBlank() -> {
                postCommentToComic(dialogFragment, token, comicId, comment)
            }
            !commentId.isNullOrBlank() -> {
                postCommentToComment(dialogFragment, token, commentId, comment)
            }
        }
        linearProgressIndicator.hide()
    }
    
    private suspend fun postCommentToComic(dialogFragment: DialogFragment, token: String, id: String, comment: String) {
        dialogFragment.requireContext().processResponse(
            dialogFragment,
            ComicComment(token, id, comment).request()
        )
    }
    
    private suspend fun postCommentToComment(dialogFragment: DialogFragment, token: String, id: String, comment: String) {
        dialogFragment.requireContext().processResponse(
            dialogFragment,
            ReplyComment(token,  id, comment).request()
        )
    }
    
    private suspend fun Context.processResponse(dialogFragment: DialogFragment, apiRequest: BaseStringApiRequest<CommentsResponseBody>) {
        if (!apiRequest.isComplete) {
            return when (apiRequest.state) {
                STATE_IO_EXCEPTION -> {
                    indefiniteSnack(getString(R.string.request_io_exception, apiRequest.message))
                }
                STATE_EXCEPTION -> {
                    indefiniteSnack(getString(R.string.request_unknown_exception, apiRequest.message))
                }
                else -> {
                    indefiniteSnack(getString(R.string.request_unexpected_state, apiRequest.message))
                }
            }
        }
        
        if (apiRequest.isErrorResponse) {
            return apiRequest.errorResponse().let { errorResponseBody ->
                indefiniteSnack(
                    getString(
                        R.string.response_error,
                        errorResponseBody.code,
                        errorResponseBody.error,
                        errorResponseBody.message,
                        errorResponseBody.detail
                    )
                )
            }
        }
        
        if (apiRequest.isEmptyResponse) {
            return indefiniteSnack(getString(R.string.response_empty))
        }
    
        dialogFragment.setFragmentResult(
            dialogFragment.getString(R.string.commenting_callback),
            bundleOf()
        )
        dialogFragment.dismissNow()
    }
    
    private class CommentingLayoutCompatImpl(binding: DialogFragmentCommentingBinding): CommentingLayoutCompat(binding)
    
    private class CommentingLayoutCompatW600dpImpl(binding: DialogFragmentCommentingBinding): CommentingLayoutCompat(binding)
    
    private class CommentingLayoutCompatW1240dpImpl(binding: DialogFragmentCommentingBinding): CommentingLayoutCompat(binding)

}