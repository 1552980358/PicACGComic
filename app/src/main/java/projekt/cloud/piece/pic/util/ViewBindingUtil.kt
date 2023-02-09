package projekt.cloud.piece.pic.util

import android.view.View.GONE
import android.view.View.VISIBLE

object ViewBindingUtil {
    
    @JvmStatic
    fun goneIfBlank(string: String?) = when {
        string.isNullOrBlank() -> GONE
        else -> VISIBLE
    }
    
    @JvmStatic
    fun isNullOrBlank(str: String?) = str.isNullOrBlank()
    
}