package projekt.cloud.piece.pic.ui.read.content

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableArrayMap
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import projekt.cloud.piece.pic.api.ApiComics.EpisodeContentResponseBody.Data.Pages.Doc
import projekt.cloud.piece.pic.databinding.LayoutRecyclerComicContentBinding

class RecyclerViewAdapter(private val docs: List<Doc>,
                          private val images: ObservableArrayMap<String, Bitmap?>):
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {
    
     class RecyclerViewHolder(private val binding: LayoutRecyclerComicContentBinding): ViewHolder(binding.root) {
        constructor(view: View): this(view.context)
        constructor(context: Context): this(LayoutInflater.from(context))
        constructor(layoutInflater: LayoutInflater): this(LayoutRecyclerComicContentBinding.inflate(layoutInflater))
        
        fun bind(doc: Doc, images: ObservableArrayMap<String, Bitmap?>) {
            binding.id = doc._id
            binding.images = images
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(docs[position], images)
    }
    
    override fun getItemCount() = docs.size
    
    private var docSize = docs.size
    
    fun notifyListUpdate() {
        notifyItemRangeInserted(docSize, docs.size)
        docSize = docs.size
    }
    
}