package projekt.cloud.piece.pic.ui.home.index

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.progressindicator.CircularProgressIndicator
import projekt.cloud.piece.pic.api.collections.CollectionsResponseBody.Data.Collection.Comic
import projekt.cloud.piece.pic.api.image.Image
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.pic.base.BaseRecyclerViewHolder
import projekt.cloud.piece.pic.databinding.LayoutRecyclerIndexBinding

class RecyclerViewAdapter(
    itemList: List<Comic>,
    private val fragment: Fragment,
    private val onClick: (Comic, AppCompatImageView) -> Unit
): BaseRecyclerViewAdapter<RecyclerViewAdapter.RecyclerViewHolder, Comic>(itemList) {
    
    class RecyclerViewHolder(parent: ViewGroup):
        BaseRecyclerViewHolder<(LayoutRecyclerIndexBinding)>(parent, LayoutRecyclerIndexBinding::class.java) {
        
        private val shapeableImageView: ShapeableImageView
            get() = binding.shapeableImageView
        
        private val circularProgressIndicator: CircularProgressIndicator
            get() = binding.circularProgressIndicator
        
        fun onBind(comic: Comic, fragment: Fragment, onClick: (Comic, AppCompatImageView) -> Unit) {
            binding.comic = comic
            binding.onClick = onClick
            binding.isLoading = true
            
            val requestListener = object: RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    loadImage(comic.cover, fragment, this)
                    return false
                }
    
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    circularProgressIndicator.hide()
                    circularProgressIndicator.isIndeterminate = false
                    binding.isLoading = false
                    return false
                }
            }
            
            loadImage(comic.cover, fragment, requestListener)
        }
        
        private fun loadImage(cover: Image, fragment: Fragment, requestListener: RequestListener<Drawable>) {
            Glide.with(fragment)
                .load(cover.getUrl())
                .addListener(requestListener)
                .into(shapeableImageView)
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(parent)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.onBind(itemList[position], fragment, onClick)
    }
    
}