package projekt.cloud.piece.pic.util

import android.app.Activity
import androidx.window.layout.WindowMetricsCalculator

object LayoutUtil {

    private const val WIDTH_MEDIUM = 600F
    private const val WIDTH_EXPANDED = 1240F

    enum class LayoutSizeMode {
        COMPACT,
        MEDIUM,
        EXPANDED
    }

    fun Activity.getLayoutSize(): LayoutSizeMode {
        val metrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
        val widthDp = metrics.bounds.width() / resources.displayMetrics.density
        return when {
            widthDp < WIDTH_MEDIUM -> LayoutSizeMode.COMPACT
            widthDp < WIDTH_EXPANDED -> LayoutSizeMode.MEDIUM
            else -> LayoutSizeMode.EXPANDED
        }
    }

}