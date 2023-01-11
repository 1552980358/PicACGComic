package projekt.cloud.piece.pic.ui.viewer

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import projekt.cloud.piece.pic.api.comics.episode.EpisodeResponseBody.EpisodeImage
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.pic.base.BaseRecyclerViewHolder
import projekt.cloud.piece.pic.databinding.LayoutRecyclerViewerBinding
import projekt.cloud.piece.pic.ui.viewer.RecyclerViewAdapter.RecyclerViewHolder

class RecyclerViewAdapter(
    episodeImageList: List<EpisodeImage>,
    private val fragment: Fragment
): BaseRecyclerViewAdapter<RecyclerViewHolder, EpisodeImage>(episodeImageList) {
    
    class RecyclerViewHolder(parent: ViewGroup): BaseRecyclerViewHolder<LayoutRecyclerViewerBinding>(parent, LayoutRecyclerViewerBinding::class.java) {
        
        private val appCompatImageView: AppCompatImageView
            get() = binding.appCompatImageView
        
        init {
            binding.isLoading = true
            binding.isError = false
            binding.recyclerViewHolder = this
        }
        
        fun onBind(fragment: Fragment, episodeImage: EpisodeImage, itemOrder: Int, itemListSize: Int) {
            binding.fragment = fragment
            binding.itemOrder = itemOrder
            binding.itemListSize = itemListSize
            loadImage(fragment, episodeImage)
        }
        
        fun onClick(fragment: Fragment, episodeImage: EpisodeImage) {
            if (binding.isLoading == true && binding.isError == true) {
                loadImage(fragment, episodeImage)
            }
        }
        
        private fun loadImage(fragment: Fragment, episodeImage: EpisodeImage) {
            binding.isLoading = true
            binding.isError = false
            
            Glide.with(fragment)
                .load(episodeImage.media.getUrl())
                .addListener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
                    ): Boolean {
                        binding.isError = true
                        return false
                    }
                    override fun onResourceReady(
                        resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean
                    ): Boolean {
                        binding.isLoading = false
                        return false
                    }
                })
                .into(appCompatImageView)
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.onBind(fragment, itemList[position], position + 1, itemListSize)
    }
    
}