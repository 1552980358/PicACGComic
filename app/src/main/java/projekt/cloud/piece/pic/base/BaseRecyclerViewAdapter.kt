package projekt.cloud.piece.pic.base

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
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
        notifyItemRangeInserted(itemListSize, itemList.size - itemListSize)
        itemListSize = itemList.size
    }
    
    override fun notifyClear() {
        notifyItemRangeRemoved(0, itemListSize)
        itemListSize = 0
    }
    
}