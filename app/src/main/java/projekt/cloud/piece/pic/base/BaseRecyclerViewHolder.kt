package projekt.cloud.piece.pic.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.pic.util.ViewBindingInflater

abstract class BaseRecyclerViewHolder<VB: ViewBinding>private constructor(
    protected val binding: VB
): ViewHolder(binding.root) {

    constructor(view: ViewGroup, clazz: Class<VB>): this(view, LayoutInflater.from(view.context), clazz)
    constructor(view: ViewGroup, layoutInflater: LayoutInflater, clazz: Class<VB>): this(
        ViewBindingInflater<VB>(clazz).inflate(layoutInflater, view, false)
    )

}