package projekt.cloud.piece.pic.ui.home.categories

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import projekt.cloud.piece.pic.api.comics.categories.CategoriesResponseBody.Category
import projekt.cloud.piece.pic.api.image.Image
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.pic.base.BaseRecyclerViewHolder
import projekt.cloud.piece.pic.databinding.LayoutRecyclerCategoriesBinding
import projekt.cloud.piece.pic.ui.home.categories.RecyclerViewAdapter.RecyclerViewHolder
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.widget.FixRatioImageView

class RecyclerViewAdapter(
    categoryList: List<Category>,
    private val fragment: Fragment,
    private val onClick: (String) -> Unit
): BaseRecyclerViewAdapter<RecyclerViewHolder, Category>(categoryList) {
    
    class RecyclerViewHolder(
        parent: ViewGroup, onClick: (String) -> Unit
    ): BaseRecyclerViewHolder<LayoutRecyclerCategoriesBinding>(parent, LayoutRecyclerCategoriesBinding::class.java) {
        
        init {
            binding.onClick = onClick
        }
        
        private val fixRatioImageView: FixRatioImageView
            get() = binding.fixRatioImageView
        
        fun onBind(category: Category, fragment: Fragment) {
            binding.title = category.title
            loadImage(category.thumb, fragment)
        }
        
        private fun loadImage(thumb: Image, fragment: Fragment) {
            fragment.lifecycleScope.ui {
                Glide.with(fragment)
                    .load(thumb.getUrl())
                    .listener(
                        object: RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
                            ): Boolean {
                                Glide.with(fragment).clear(fixRatioImageView)
                                loadImage(thumb, fragment)
                                return false
                            }
                            override fun onResourceReady(
                                resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        }
                    )
                    .into(fixRatioImageView)
            }
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent, onClick)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.onBind(itemList[position], fragment)
    }
    
}