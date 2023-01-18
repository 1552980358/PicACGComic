package projekt.cloud.piece.pic.ui.home.categories

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import projekt.cloud.piece.pic.api.comics.categories.CategoriesResponseBody.Category
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.pic.base.BaseRecyclerViewHolder
import projekt.cloud.piece.pic.databinding.LayoutRecyclerCategoriesBinding
import projekt.cloud.piece.pic.ui.home.categories.RecyclerViewAdapter.RecyclerViewHolder
import projekt.cloud.piece.pic.widget.FixRatioImageView

class RecyclerViewAdapter(
    categoryList: List<Category>,
    private val fragment: Fragment,
    private val onClick: (String) -> Unit
): BaseRecyclerViewAdapter<RecyclerViewHolder, Category>(categoryList) {
    
    class RecyclerViewHolder(
        parent: ViewGroup, private val fragment: Fragment, onClick: (String) -> Unit
    ): BaseRecyclerViewHolder<LayoutRecyclerCategoriesBinding>(parent, LayoutRecyclerCategoriesBinding::class.java) {
        
        init {
            binding.onClick = onClick
        }
        
        private val fixRatioImageView: FixRatioImageView
            get() = binding.fixRatioImageView
        
        fun onBind(category: Category) {
            binding.title = category.title
            Glide.with(fragment)
                .load(category.thumb.getUrl())
                .into(fixRatioImageView)
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent, fragment, onClick)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }
    
}