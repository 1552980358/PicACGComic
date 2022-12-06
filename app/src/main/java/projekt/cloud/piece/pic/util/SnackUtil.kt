package projekt.cloud.piece.pic.util

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar

typealias SnackBlock = Snackbar.() -> Unit

object SnackUtil {

    fun View.snack(@StringRes resId: Int, length: Int = LENGTH_SHORT, block: SnackBlock = {}) =
        Snackbar.make(this, resId, length).apply(block)

    fun View.snack(message: String, length: Int = LENGTH_SHORT, block: SnackBlock = {}) =
        Snackbar.make(this, message, length).apply(block)

    fun View.showSnack(@StringRes resId: Int, length: Int = LENGTH_SHORT, block: SnackBlock = {}) =
        snack(resId, length, block).show()

    fun View.showSnack(message: String, length: Int = LENGTH_SHORT, block: SnackBlock = {}) =
        snack(message, length, block).show()

}