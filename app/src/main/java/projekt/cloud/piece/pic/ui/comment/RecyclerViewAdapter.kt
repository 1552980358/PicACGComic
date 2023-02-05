package projekt.cloud.piece.pic.ui.comment

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import projekt.cloud.piece.pic.api.comments.children.ChildrenResponseBody.Comment
import projekt.cloud.piece.pic.api.image.Image
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.pic.base.BaseRecyclerViewHolder
import projekt.cloud.piece.pic.databinding.LayoutRecyclerCommentBinding
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.widget.DefaultedImageView

class RecyclerViewAdapter(
    commentList: List<Comment>, private val fragment: Fragment, private val likeClicked: (String) -> Unit
): BaseRecyclerViewAdapter<RecyclerViewAdapter.RecyclerViewHolder, Comment>(commentList) {
    
    class RecyclerViewHolder(
        parent: ViewGroup, recyclerViewAdapter: RecyclerViewAdapter
    ): BaseRecyclerViewHolder<LayoutRecyclerCommentBinding>(parent, LayoutRecyclerCommentBinding::class.java) {
        
        init {
            binding.adapter = recyclerViewAdapter
        }
        
        private val defaultedImageView: DefaultedImageView
            get() = binding.defaultedImageView
        
        fun bind(comment: Comment, fragment: Fragment) {
            binding.isLiked  = comment.isLiked
            if (binding.id != comment.id) {
                binding.id = comment.id
                binding.user = comment.user.name
                binding.level = comment.user.level
                binding.comment = comment.comment
                binding.createDate = comment.createDateStr
                loadAvatar(fragment, comment.user.avatar)
            }
        }
        
        private fun loadAvatar(fragment: Fragment, avatar: Image?) {
            fragment.lifecycleScope.ui {
                if (avatar == null) {
                    return@ui defaultedImageView.switchToDefault()
                }
                Glide.with(fragment)
                    .load(avatar.getUrl())
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
                            ) = true
                        }
                    )
                    .into(defaultedImageView)
            }
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(parent, this)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(itemList[position], fragment)
    }
    
    fun likeButtonClicked(id: String) {
        likeClicked.invoke(id)
    }
    
}