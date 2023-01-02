package projekt.cloud.piece.pic.ui.home.index

import android.graphics.Bitmap
import android.view.ViewGroup
import androidx.databinding.ObservableArrayMap
import projekt.cloud.piece.pic.api.collections.CollectionsResponseBody.Data.Collection.Comic
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.pic.base.BaseRecyclerViewHolder
import projekt.cloud.piece.pic.databinding.LayoutRecyclerIndexBinding

class RecyclerViewAdapter(
    itemList: List<Comic>,
    private val coverMap: ObservableArrayMap<String, Bitmap?>,
    private val onClick: (Comic) -> Unit
): BaseRecyclerViewAdapter<RecyclerViewAdapter.RecyclerViewHolder, Comic>(itemList) {
    
    class RecyclerViewHolder(parent: ViewGroup): BaseRecyclerViewHolder<(LayoutRecyclerIndexBinding)>(parent, LayoutRecyclerIndexBinding::class.java) {
        fun onBind(comic: Comic, coverMap: ObservableArrayMap<String, Bitmap?>, onClick: (Comic) -> Unit) {
            binding.comic = comic
            binding.onClick = onClick
            if (binding.coverMap != coverMap) {
                binding.coverMap = coverMap
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder(parent)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.onBind(itemList[position], coverMap, onClick)
    }
    
}