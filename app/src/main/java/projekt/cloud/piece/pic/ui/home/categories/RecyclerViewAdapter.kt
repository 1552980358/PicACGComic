package projekt.cloud.piece.pic.ui.home.categories

import android.view.ViewGroup
import projekt.cloud.piece.pic.api.comics.categories.CategoriesResponseBody.Category
import projekt.cloud.piece.pic.base.BaseRecyclerViewAdapter
import projekt.cloud.piece.pic.base.BaseRecyclerViewHolder
import projekt.cloud.piece.pic.databinding.LayoutRecyclerCategoriesBinding
import projekt.cloud.piece.pic.ui.home.categories.RecyclerViewAdapter.RecyclerViewHolder

class RecyclerViewAdapter(
    categoryList: List<Category>,
    private val onClick: (String) -> Unit
): BaseRecyclerViewAdapter<RecyclerViewHolder, Category>(categoryList) {
    
    class RecyclerViewHolder(
        parent: ViewGroup, onClick: (String) -> Unit
    ): BaseRecyclerViewHolder<LayoutRecyclerCategoriesBinding>(parent, LayoutRecyclerCategoriesBinding::class.java) {
        
        init {
            binding.onClick = onClick
        }
        
        fun onBind(category: Category) {
            binding.title = category.title
            binding.url = category.thumb.getUrl()
        }
        
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent, onClick)
    
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }
    
}