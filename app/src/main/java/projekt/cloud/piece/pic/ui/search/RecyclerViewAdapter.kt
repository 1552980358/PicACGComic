package projekt.cloud.piece.pic.ui.search

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiComics.ComicsResponseBody.Data.Comics.Doc
import projekt.cloud.piece.pic.databinding.LayoutRecyclerSearchBinding
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui

class RecyclerViewAdapter(private val docs: List<Doc>,
                          private val covers: MutableMap<String, Bitmap?>,
                          private val onClick: (Doc, View) -> Unit):
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    inner class RecyclerViewHolder(private val binding: LayoutRecyclerSearchBinding):
        RecyclerView.ViewHolder(binding.root), OnClickListener {
        
        constructor(view: View): this(view.context)
        constructor(context: Context): this(LayoutInflater.from(context))
        constructor(inflater: LayoutInflater): this(LayoutRecyclerSearchBinding.inflate(inflater))
        
        private var job: Job? = null
        
        init {
            binding.root.setOnClickListener(this)
        }
        
        fun setDoc(doc: Doc) {
            binding.doc = doc
            binding.root.transitionName = binding.root.resources.getString(R.string.comic_detail_transition_prefix) + doc._id
            when {
                covers.containsKey(doc._id) -> {
                    binding.bitmap = covers[doc._id]
                }
                else -> {
                    binding.bitmap = BitmapFactory.decodeResource(binding.root.resources, R.drawable.ic_round_image_24)
                    job?.cancel()
                    job = ui {
                        val bitmap = withContext(io) {
                            doc.thumb.bitmap
                        }
                        binding.bitmap = bitmap
                        covers[doc._id] = bitmap
                        job = null
                    }
                }
            }

        }
    
        override fun onClick(v: View) {
            binding.doc?.let {
                onClick.invoke(it, v)
            }
        }
        
    }
    
    private var docSize = docs.size

    fun notifyListUpdate() {
        notifyItemRangeInserted(docSize, docs.size - docSize)
        docSize = docs.size
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.setDoc(docs[position])
    }
    
    override fun getItemCount() = docSize
    
}