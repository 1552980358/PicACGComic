package projekt.cloud.piece.pic.ui.read.content

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiComics.EpisodeContentResponseBody.Data.Pages.Doc
import projekt.cloud.piece.pic.databinding.LayoutRecyclerComicContentBinding
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui

class RecyclerViewAdapter(private val lifecycleScope: LifecycleCoroutineScope,
                          private val docs: List<Doc>,
                          private val images: MutableMap<String, Bitmap?>):
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {
    
     class RecyclerViewHolder(private val binding: LayoutRecyclerComicContentBinding): ViewHolder(binding.root) {
        constructor(view: View): this(view.context)
        constructor(context: Context): this(LayoutInflater.from(context))
        constructor(layoutInflater: LayoutInflater): this(LayoutRecyclerComicContentBinding.inflate(layoutInflater))
        
        private var job: Job? = null
        
        fun setDoc(lifecycleScope: LifecycleCoroutineScope, doc: Doc, images: MutableMap<String, Bitmap?>) {
            val id = doc._id
            when {
                images.containsKey(id) -> binding.bitmap = images[id]
                else -> {
                    job?.cancel()
                    job = lifecycleScope.ui {
                        val image = withContext(io) {
                            doc.media.bitmap
                        }
                        images[id] = image
                        binding.bitmap = image
                        job = null
                    }
                }
            }
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.setDoc(lifecycleScope, docs[position], images)
    }
    
    override fun getItemCount() = docs.size
    
    private var docSize = docs.size
    
    fun notifyListUpdate() {
        notifyItemRangeInserted(docSize, docs.size)
        docSize = docs.size
    }
    
}