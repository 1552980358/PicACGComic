package projekt.cloud.piece.pic.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import projekt.cloud.piece.pic.R

open class DefaultedImageView(context: Context, attributeSet: AttributeSet): ShapeableImageView(context, attributeSet) {
    
    val defaultDrawable: Int
    
    init {
        resources.obtainAttributes(attributeSet, R.styleable.DefaultedImageView).use { typedArray ->
            defaultDrawable = typedArray.getResourceId(R.styleable.DefaultedImageView_defaultImage, 0)
            switchToDefault()
        }
    }
    
    fun switchToDefault() {
        setImageResource(defaultDrawable)
    }
    
}