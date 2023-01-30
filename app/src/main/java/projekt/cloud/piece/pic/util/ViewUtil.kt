package projekt.cloud.piece.pic.util

import android.view.View

object ViewUtil {
    
    private const val SCROLL_VERTICAL_UP = -1
    private const val SCROLL_VERTICAL_DOWN = 1
    
    @JvmStatic
    val View.canScrollUp: Boolean
        get() = canScrollVertically(SCROLL_VERTICAL_UP)
    
    @JvmStatic
    val View.canScrollDown: Boolean
        get() = canScrollVertically(SCROLL_VERTICAL_DOWN)
    
}