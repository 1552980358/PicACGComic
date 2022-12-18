package projekt.cloud.piece.pic.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView.ViewHolder

object ViewHolderUtil {
    
    @JvmStatic
    val ViewHolder.context: Context
        get() = itemView.context
    
    @JvmStatic
    fun ViewHolder.getString(@StringRes resId: Int) =
        context.getString(resId)
    
    @JvmStatic
    fun ViewHolder.getString(@StringRes resId: Int, vararg formatArgs: Any?) =
        context.getString(resId, *formatArgs)
    
    @JvmStatic
    fun ViewHolder.setContainerTransitionName(transitionName: String) {
        itemView.transitionName = transitionName
    }
    
}