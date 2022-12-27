package projekt.cloud.piece.pic.util

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceManager

object ActivityUtil {
    
    @JvmStatic
    fun <T: Activity> Activity.startActivity(activityClass: Class<T>, bundle: Bundle) {
        startActivity(
            Intent(this, activityClass).putExtras(bundle)
        )
    }
    
    @JvmStatic
    val Activity.defaultSharedPreference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(this)
    
}