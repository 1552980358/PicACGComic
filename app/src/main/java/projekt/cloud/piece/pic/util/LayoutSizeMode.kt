package projekt.cloud.piece.pic.util

import android.app.Activity
import androidx.window.layout.WindowMetricsCalculator

enum class LayoutSizeMode {
    COMPACT,
    MEDIUM,
    EXPANDED;

    companion object LayoutSizeModeUtil {

        private const val WIDTH_MEDIUM = 600F
        private const val WIDTH_EXPANDED = 1240F

        @JvmStatic
        fun Activity.getLayoutSize(): LayoutSizeMode {
            val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
            val widthDp = metrics.bounds.width() / resources.displayMetrics.density
            return when {
                widthDp < WIDTH_MEDIUM -> COMPACT
                widthDp < WIDTH_EXPANDED -> MEDIUM
                else -> EXPANDED
            }
        }

    }

}