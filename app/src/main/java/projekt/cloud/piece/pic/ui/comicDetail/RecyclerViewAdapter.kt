package projekt.cloud.piece.pic.ui.comicDetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiComics.EpisodeResponseBody.Data.Episode.Doc
import projekt.cloud.piece.pic.base.BaseRecyclerViewHolder
import projekt.cloud.piece.pic.databinding.LayoutRecyclerComicDetailBinding

class RecyclerViewAdapter(private val docs: List<Doc>,
                          private val onClick: (Doc, View) -> Unit):
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {
    
    inner class RecyclerViewHolder(private val binding: LayoutRecyclerComicDetailBinding):
        BaseRecyclerViewHolder(binding.root), OnClickListener {
        
        constructor(view: View): this(view.context)
        constructor(context: Context): this(LayoutInflater.from(context))
        constructor(layoutInflater: LayoutInflater): this(LayoutRecyclerComicDetailBinding.inflate(layoutInflater))
        
        init {
            binding.root.setOnClickListener(this)
        }
        
        fun setData(descendingIndex: String, doc: Doc) {
            binding.root.transitionName = context.getString(R.string.read_transition_prefix) + doc._id
            binding.index = descendingIndex
            binding.doc = doc
        }
    
        override fun onClick(v: View) {
            binding.doc?.let { onClick.invoke(it, v) }
        }
        
    }
    
    private var docSize = docs.size
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.setData((itemCount - position).toString(), docs[position])
    }
    
    override fun getItemCount() = docs.size
    
    fun notifyDataUpdated() {
        notifyItemRangeInserted(docSize, docs.size - docSize)
        docSize = docs.size
    }
    
}