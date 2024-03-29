package projekt.cloud.piece.pic.util

import android.content.Context
import projekt.cloud.piece.pic.R

enum class ScreenDensity(val value: Int) {
    COMPACT(0),
    MEDIUM(1),
    EXPANDED(2);
    
    companion object ScreenDensityUtil {
        
        @JvmStatic
        val Context.screenDensity: ScreenDensity
            get() = when {
                resources.getBoolean(R.bool.screen_density_medium) -> MEDIUM
                resources.getBoolean(R.bool.screen_density_expanded) -> EXPANDED
                else -> COMPACT
            }
        
    }
    
}