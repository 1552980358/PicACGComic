package projekt.cloud.piece.pic.ui.comment

import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.Menu.NONE
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_ALWAYS
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.comments.children.ChildrenResponseBody.Comment
import projekt.cloud.piece.pic.api.common.LikeResponseBody
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter.BaseRecyclerViewAdapterUtil.adapterInterface
import projekt.cloud.piece.pic.base.SnackLayoutCompat
import projekt.cloud.piece.pic.databinding.FragmentCommentBinding
import projekt.cloud.piece.pic.util.AdapterInterface
import projekt.cloud.piece.pic.util.CoroutineUtil.default
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.SerializeUtil.decodeJson
import projekt.cloud.piece.pic.widget.DefaultedImageView

abstract class CommentLayoutCompat private constructor(
    protected val binding: FragmentCommentBinding
): SnackLayoutCompat(), AdapterInterface {
    
    companion object CommentLayoutCompatUtil {
        @JvmStatic
        fun FragmentCommentBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> CommentLayoutCompatImpl(this)
            MEDIUM -> CommentLayoutCompatImpl(this)
            EXPANDED -> CommentLayoutCompatW1240Impl(this)
        }
    
        private const val INDEX_NOT_FOUND = -1
    }
    
    override val snackContainer: View
        get() = binding.coordinatorLayout
    
    private val recyclerView: RecyclerView
        get() = binding.recyclerView
    
    private val defaultedImageView: DefaultedImageView
        get() = binding.defaultedImageView
    
    private val toolbar: MaterialToolbar
        get() = binding.materialToolbar
    
    fun setupAvatar(fragment: Fragment, commentViewModel: CommentViewModel) {
        commentViewModel.comment.observe(fragment.viewLifecycleOwner) { comment ->
            when (val avatar = comment?.user?.avatar) {
                null -> {
                    defaultedImageView.switchToDefault()
                }
                else -> {
                    Glide.with(fragment)
                        .load(avatar.getUrl())
                        .listener(
                            object: RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
                                ): Boolean {
                                    defaultedImageView.switchToDefault()
                                    return false
                                }
                                override fun onResourceReady(
                                    resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean
                                ) = false
                            }
                        )
                        .into(defaultedImageView)
                }
            }
        }
    }
    
    fun setupRecyclerView(fragment: Fragment, mainViewModel: MainViewModel, commentViewModel: CommentViewModel) {
        recyclerView.adapter = RecyclerViewAdapter(commentViewModel.commentList, fragment) { id ->
            mainViewModel.account.value?.let { account ->
                if (account.isSignedIn) {
                    commentViewModel.scopedPostLikeComic(account.token, id, fragment.lifecycleScope)
                }
            }
        }
    }
    
    fun setupActionBar(fragment: Fragment) {
        fragment.setSupportActionBar(toolbar)
        val likeId = View.generateViewId()
        fragment.requireActivity().addMenuProvider(
            object: MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menu.clear()
                    menu.add(NONE, likeId, NONE, R.string.comment_like)
                        .setIcon(R.drawable.ic_round_favorite_border_24)
                        .setShowAsAction(SHOW_AS_ACTION_ALWAYS)
                }
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return false
                }
            },
            fragment.viewLifecycleOwner,
            CREATED
        )
    }
    
    override fun notifyUpdate() {
        recyclerView.adapterInterface.notifyUpdate()
    }
    
    override fun notifyUpdate(index: Int, value: Any?) {
        recyclerView.adapterInterface.notifyUpdate(index, value)
    }
    
    fun updateCommentLike(fragment: Fragment, commentViewModel: CommentViewModel, id: String?, responseContent: String?) {
        if (!id.isNullOrBlank() && !responseContent.isNullOrBlank()) {
            fragment.lifecycleScope.ui {
                updateCommentLike(
                    commentViewModel.commentList,
                    id,
                    withContext(default) {
                        responseContent.decodeJson<LikeResponseBody>().isLiked
                    }
                )
            }
        }
    }
    
    private suspend fun updateCommentLike(commentList: List<Comment>, id: String, isLiked: Boolean) {
        val index = withContext(default) {
            commentList.indexOfFirst { it.id == id }
        }
        if (index != INDEX_NOT_FOUND) {
            commentList[index].let { comment ->
                comment.isLiked = isLiked
                notifyUpdate(index, comment)
            }
        }
    }
    
    private class CommentLayoutCompatImpl(binding: FragmentCommentBinding): CommentLayoutCompat(binding)
    
    private class CommentLayoutCompatW600dpImpl(binding: FragmentCommentBinding): CommentLayoutCompat(binding)
    
    private class CommentLayoutCompatW1240Impl(binding: FragmentCommentBinding): CommentLayoutCompat(binding)
    
}