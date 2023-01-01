package projekt.cloud.piece.pic.util

import android.content.Intent
import android.os.Build
import android.os.Bundle
import java.io.Serializable

object ExtraUtil {
    
    @JvmStatic
    inline fun <reified S: Serializable> Intent.getSerializableOf(name: String): S? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                getSerializableExtra(name, S::class.java)
            }
            else -> {
                @Suppress("DEPRECATION")
                getSerializableExtra(name) as? S
            }
        }
    }
    
    @JvmStatic
    inline fun <reified S: Serializable> Bundle.getSerializableOf(name: String): S? {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                getSerializable(name, S::class.java)
            }
            else -> {
                @Suppress("DEPRECATION")
                getSerializable(name) as? S
            }
        }
    }
    
}