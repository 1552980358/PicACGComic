package projekt.cloud.piece.pic.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableArrayMap
import androidx.recyclerview.widget.RecyclerView
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.api.ApiCategories.CategoriesResponseBody.Data.Category
import projekt.cloud.piece.pic.databinding.LayoutRecyclerHomeBinding
import projekt.cloud.piece.pic.ui.home.RecyclerViewAdapter.RecyclerViewHolder
import projekt.cloud.piece.pic.util.ViewHolderUtil.getString
import projekt.cloud.piece.pic.util.ViewHolderUtil.setContainerTransitionName

class RecyclerViewAdapter(private val categories: List<Category>,
                          private val covers: ObservableArrayMap<String, Bitmap?>,
                          private val onClick: (View, Category) -> Unit): RecyclerView.Adapter<RecyclerViewHolder>() {

    class RecyclerViewHolder(private val binding: LayoutRecyclerHomeBinding): RecyclerView.ViewHolder(binding.root) {
        
        constructor(parent: ViewGroup): this(parent.context)
        constructor(context: Context): this(LayoutInflater.from(context))
        constructor(layoutInflater: LayoutInflater): this(LayoutRecyclerHomeBinding.inflate(layoutInflater))

        fun bind(category: Category, covers: ObservableArrayMap<String, Bitmap?>, onClick: (View, Category) -> Unit) {
            setContainerTransitionName(getString(R.string.list_transition_item, category.title))
            binding.category = category
            binding.onClick = onClick
            binding.covers = covers
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecyclerViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(categories[position], covers, onClick)
    }

    override fun getItemCount() = categories.size

}