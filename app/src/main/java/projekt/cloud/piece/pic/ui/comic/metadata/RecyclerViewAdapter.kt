package projekt.cloud.piece.pic.ui.comic.metadata

import android.view.ViewGroup
import projekt.cloud.piece.pic.api.comics.episodes.EpisodesResponseBody.Episode
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.pic.base.BaseRecyclerViewHolder
import projekt.cloud.piece.pic.databinding.LayoutRecyclerMetadataBinding

class RecyclerViewAdapter(
    episodeList: List<Episode>,
    private val onClick: (Episode) -> Unit
): BaseRecyclerViewAdapter<RecyclerViewAdapter.RecyclerViewHolder, Episode>(episodeList) {
    
    class RecyclerViewHolder(
        viewGroup: ViewGroup,
        onClick: (Episode) -> Unit
    ): BaseRecyclerViewHolder<LayoutRecyclerMetadataBinding>(viewGroup, LayoutRecyclerMetadataBinding::class.java) {
        
        init {
            binding.onClick = onClick
        }
        
        fun onBind(episode: Episode) {
            binding.episode = episode
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent, onClick)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }
    
}