package projekt.cloud.piece.pic.base

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar

abstract class SnackLayoutCompat {
    
    private var _snackBar: Snackbar? = null
    
    abstract val snackContainer: View
    
    open val snackAnchor: View? = null
    
    fun shortSnack(message: String, resId: String? = null, action: (() -> Unit)? = null) =
        createSnack(message, LENGTH_SHORT, resId, action)
    
    fun longSnack(message: String, resId: String? = null, action: (() -> Unit)? = null) =
        createSnack(message, LENGTH_LONG, resId, action)
    
    fun indefiniteSnack(message: String, resId: String? = null, action: (() -> Unit)? = null) =
        createSnack(message, LENGTH_INDEFINITE, resId, action)
    
    private fun createSnack(message: String, length: Int, resId: String? = null, action: (() -> Unit)? = null) {
        Snackbar.make(snackContainer, message, length).apply {
            if (resId != null) {
                setAction(resId) { action?.invoke() }
            }
            clearSnack()
            _snackBar = this
        }.show()
    }
    
    fun clearSnack() {
        _snackBar?.dismiss()
        _snackBar = null
    }
    
}