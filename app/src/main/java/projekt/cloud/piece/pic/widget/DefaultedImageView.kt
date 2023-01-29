package projekt.cloud.piece.pic.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import projekt.cloud.piece.pic.R

open class DefaultedImageView(context: Context, attributeSet: AttributeSet): ShapeableImageView(context, attributeSet) {
    
    private val defaultDrawable: Drawable?
    
    init {
        resources.obtainAttributes(attributeSet, R.styleable.GlideShapeableImageView).use { typedArray ->
            defaultDrawable = typedArray.getDrawable(R.styleable.GlideShapeableImageView_defaultImage)
            switchToDefault()
        }
    }
    
    fun switchToDefault() {
        setImageDrawable(defaultDrawable)
    }
    
}