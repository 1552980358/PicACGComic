package projekt.cloud.piece.pic.ui.search

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableArrayMap
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiComics.AdvancedSearchResponseBody.Data.Comics.Doc
import projekt.cloud.piece.pic.databinding.LayoutRecyclerSearchBinding
import projekt.cloud.piece.pic.util.ViewHolderUtil.getString
import projekt.cloud.piece.pic.util.ViewHolderUtil.setContainerTransitionName

class RecyclerViewAdapter(
    private val comicList: List<Doc>,
    private val coverImages: ObservableArrayMap<String, Bitmap?>,
    private val onClick: (View, Doc) -> Unit
): RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    class RecyclerViewHolder(private val binding: LayoutRecyclerSearchBinding):
        RecyclerView.ViewHolder(binding.root) {
        
        constructor(view: View): this(view.context)
        constructor(context: Context): this(LayoutInflater.from(context))
        constructor(inflater: LayoutInflater): this(LayoutRecyclerSearchBinding.inflate(inflater))
        
        fun bind(comic: Doc, coverImages: ObservableArrayMap<String, Bitmap?>, onClick: (View, Doc) -> Unit) {
            setContainerTransitionName(getString(R.string.comic_detail_transition_prefix) + comic.id)
            binding.comic = comic
            binding.onClick = onClick
            binding.coverImages = coverImages
        }
        
    }
    
    private var docSize = comicList.size

    fun notifyListUpdate() {
        notifyItemRangeInserted(docSize, comicList.size - docSize)
        docSize = comicList.size
    }
    
    fun notifyListReset() {
        notifyItemRangeRemoved(docSize, docSize)
        docSize = comicList.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(comicList[position], coverImages, onClick)
    }
    
    override fun getItemCount() = docSize
    
}