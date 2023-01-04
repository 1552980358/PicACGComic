package projekt.cloud.piece.pic.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.View.MeasureSpec.UNSPECIFIED
import androidx.databinding.BindingAdapter
import kotlin.math.min

class InsetSpaceView(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {
    
    companion object {
        private const val INSET_DEFAULT = 0
        
        private const val BINDING_ADAPTER_INSET_HORIZONTAL = "inset_horizontal"
        private const val BINDING_ADAPTER_INSET_VERTICAL = "inset_vertical"
        
        @JvmStatic
        @BindingAdapter(BINDING_ADAPTER_INSET_HORIZONTAL)
        fun InsetSpaceView.setInsetHorizontal(insetHorizontal: Int?) {
            insetHorizontal?.let {
                this.insetHorizontal = insetHorizontal
            }
        }
    
        @JvmStatic
        @BindingAdapter(BINDING_ADAPTER_INSET_VERTICAL)
        fun InsetSpaceView.setInsetVertical(insetVertical: Int?) {
            insetVertical?.let {
                this.insetVertical = insetVertical
            }
        }
        
    }
    
    @Volatile
    var insetHorizontal = INSET_DEFAULT
        @Synchronized
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }
    
    @Volatile
    var insetVertical = INSET_DEFAULT
        @Synchronized
        set(value) {
            if (field != value) {
                field = value
                requestLayout()
            }
        }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.e("", "")
        setMeasuredDimension(
            when (MeasureSpec.getMode(widthMeasureSpec)) {
                UNSPECIFIED -> insetHorizontal
                else -> min(insetHorizontal, MeasureSpec.getSize(widthMeasureSpec))
            },
            when (MeasureSpec.getMode(heightMeasureSpec)) {
                UNSPECIFIED -> insetVertical
                else -> min(insetVertical, MeasureSpec.getSize(heightMeasureSpec))
            }
        )
    }
    
}