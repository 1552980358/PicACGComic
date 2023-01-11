package projekt.cloud.piece.pic.util

import android.app.Activity
import android.content.Intent
import android.os.Bundle

object ActivityUtil {
    
    @JvmStatic
    fun <T: Activity> Activity.startActivity(activityClass: Class<T>, bundle: Bundle) {
        startActivity(
            Intent(this, activityClass).putExtras(bundle)
        )
    }
    
    @JvmStatic
    fun <T: Activity> Activity.startActivity(activityClass: Class<T>) {
        startActivity(Intent(this, activityClass))
    }
    
}