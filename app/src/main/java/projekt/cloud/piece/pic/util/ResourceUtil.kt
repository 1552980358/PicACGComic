package projekt.cloud.piece.pic.util

import android.content.res.Resources

object ResourceUtil {
    
    @JvmStatic
    fun Resources.dpToPx(dp: Int): Int {
        return (displayMetrics.density * dp + 0.5F).toInt()
    }
    
}