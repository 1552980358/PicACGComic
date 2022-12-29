package projekt.cloud.piece.pic.util

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method

class ViewBindingInflater<VB: ViewBinding>(private val viewBindingClass: Class<VB>) {
    
    companion object ViewBindingInflaterUtil {
        const val VIEW_BINDING_METHOD_INFLATE = "inflate"
    }
    
    private val methodInflate: Method
        get() = viewBindingClass.getDeclaredMethod(
            VIEW_BINDING_METHOD_INFLATE,
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
    
    @Suppress("UNCHECKED_CAST")
    fun inflate(inflater: LayoutInflater, viewGroup: ViewGroup?, attachToRoot: Boolean) =
        methodInflate.invoke(null, inflater, viewGroup, attachToRoot) as VB
    
    fun inflate(inflater: LayoutInflater) = inflate(inflater, null, false)
    
}