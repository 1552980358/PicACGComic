package projekt.cloud.piece.pic.ui.viewer

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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
        
        fun onBind(fragment: Fragment, episodeImage: EpisodeImage) {
            Glide.with(fragment)
                .load(episodeImage.media.getUrl())
                .into(appCompatImageView)
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.onBind(fragment, itemList[position])
    }
    
}