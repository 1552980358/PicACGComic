package projekt.cloud.piece.pic.ui.home.search

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.imageview.ShapeableImageView
import projekt.cloud.piece.pic.api.comics.search.AdvancedSearchResponseBody.Comic
import projekt.cloud.piece.pic.api.image.Image
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.pic.base.BaseRecyclerViewHolder
import projekt.cloud.piece.pic.databinding.LayoutRecyclerSearchBinding
import projekt.cloud.piece.pic.ui.home.search.RecyclerViewAdapter.RecyclerViewHolder
import projekt.cloud.piece.pic.util.CoroutineUtil.ui

class RecyclerViewAdapter(
    comicList: List<Comic>,
    private val fragment: Fragment,
    private val onClick: (String, String, AppCompatImageView) -> Unit
): BaseRecyclerViewAdapter<RecyclerViewHolder, Comic>(comicList) {
    
    class RecyclerViewHolder(
        parent: ViewGroup, private val fragment: Fragment, onClick: (String, String, AppCompatImageView) -> Unit
    ): BaseRecyclerViewHolder<LayoutRecyclerSearchBinding>(parent, LayoutRecyclerSearchBinding::class.java) {
        
        init {
            binding.onClick = onClick
        }
        
        private val shapeableImageView: ShapeableImageView
            get() = binding.shapeableImageView
        
        fun onBind(comic: Comic) {
            binding.id = comic.id
            binding.title = comic.title
            binding.author = comic.author
            binding.categories = comic.categories
            binding.likes = comic.likesCount
            fragment.lifecycleScope.ui {
                loadCover(comic.cover, fragment)
            }
        }
        
        fun loadCover(cover: Image, fragment: Fragment) {
            Glide.with(fragment)
                .load(cover.getUrl())
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        fragment.lifecycleScope.ui {
                            Glide.with(fragment)
                                .clear(shapeableImageView)
                            loadCover(cover, fragment)
                        }
                        return false
                    }
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                })
                .into(shapeableImageView)
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent, fragment, onClick)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }
    
}