package projekt.cloud.piece.pic.base

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import kotlin.math.abs
import projekt.cloud.piece.pic.util.AdapterInterface

abstract class BaseRecyclerViewAdapter<VH: ViewHolder, T>(protected val itemList: List<T>): RecyclerView.Adapter<VH>(), AdapterInterface {
    
    companion object BaseRecyclerViewAdapterUtil {
        
        @JvmStatic
        val RecyclerView.adapterInterface: AdapterInterface
            get() = adapter as AdapterInterface
        
    }
    
    protected var itemListSize = itemList.size
    
    override fun getItemCount() = itemListSize
    
    override fun notifyUpdate() {
        val itemSize = itemList.size
        val itemDiff = abs(itemSize - itemListSize)
        when {
            itemListSize < itemSize -> notifyItemRangeInserted(itemListSize, itemDiff)
            itemListSize > itemSize -> notifyItemRangeRemoved(itemSize, itemDiff)
        }
        itemListSize = itemSize
        notifyItemRangeChanged(0, itemListSize)
    }
    
    override fun notifyClear() {
        notifyItemRangeRemoved(0, itemListSize)
        itemListSize = 0
    }
    
    override fun notifyUpdate(index: Int) {
        notifyItemChanged(index)
    }
    
}