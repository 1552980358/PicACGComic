package projekt.cloud.piece.pic.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object ContextUtil {
    
    @JvmStatic
    val Context.defaultSharedPreference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this)
    
}