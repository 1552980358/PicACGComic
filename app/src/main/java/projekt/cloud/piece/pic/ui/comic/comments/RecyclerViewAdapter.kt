package projekt.cloud.piece.pic.ui.comic.comments

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.comics.comments.CommentsResponseBody.Comment
import projekt.cloud.piece.pic.api.image.Image
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.pic.base.BaseRecyclerViewHolder
import projekt.cloud.piece.pic.databinding.LayoutRecyclerCommentsBinding
import projekt.cloud.piece.pic.util.CoroutineUtil.default
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.SerializeUtil.encodeJson
import projekt.cloud.piece.pic.widget.DefaultedImageView

class RecyclerViewAdapter(
    commentList: List<Comment>,
    private val fragment: Fragment,
    private val onClick: (String) -> Unit
): BaseRecyclerViewAdapter<RecyclerViewAdapter.RecyclerViewHolder, Comment>(commentList) {
    
    private lateinit var navController: NavController
    fun setNavController(navController: NavController) {
        this.navController = navController
    }

    class RecyclerViewHolder(
        parent: ViewGroup, viewHolder: RecyclerViewAdapter
    ): BaseRecyclerViewHolder<LayoutRecyclerCommentsBinding>(parent, LayoutRecyclerCommentsBinding::class.java) {
        
        init {
            binding.viewHolder = viewHolder
        }
        
        private val defaultedImageView: DefaultedImageView
            get() = binding.defaultedImageView
        
        fun onBind(comment: Comment, fragment: Fragment) {
            val id = binding.comment?.id
            binding.comment = comment
            if (id != comment.id) {
                loadAvatarImage(comment.user.avatar, fragment)
            }
        }
        
        private fun loadAvatarImage(image: Image?, fragment: Fragment) {
            if (image == null) {
                return defaultedImageView.switchToDefault()
            }
            fragment.lifecycleScope.ui {
                Glide.with(fragment)
                    .load(image.getUrl())
                    .placeholder(defaultedImageView.defaultDrawable)
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
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent, this)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.onBind(itemList[position], fragment)
    }
    
    fun likeButtonClicked(id: String) {
        onClick.invoke(id)
    }
    
    fun navigateToComment(comment: Comment) {
        fragment.lifecycleScope.ui {
            navController.navigate(
                R.id.comment,
                bundleOf(
                    fragment.getString(R.string.comment_arg_id) to comment.id,
                    fragment.getString(R.string.comment_arg_comment) to withContext(default) {
                        comment.encodeJson()
                    }
                )
            )
        }
    }
    
}