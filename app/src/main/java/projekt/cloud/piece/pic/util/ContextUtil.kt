package projekt.cloud.piece.pic.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import projekt.cloud.piece.pic.util.ResourceUtil.dpToPx

object ContextUtil {
    
    @JvmStatic
    val Context.defaultSharedPreference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this)
    
    @JvmStatic
    fun Context.dpToPx(dp: Int) = resources.dpToPx(dp)
    
}