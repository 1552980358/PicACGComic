package projekt.cloud.piece.pic.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View.MeasureSpec.UNSPECIFIED
import kotlin.math.min
import projekt.cloud.piece.pic.R

class FixRatioImageView(context: Context, attributeSet: AttributeSet): GlideShapeableImageView(context, attributeSet) {
    
    private var ratioX: Int
    private var ratioY: Int
    
    private val defaultRatio = resources.getInteger(R.integer.fix_ratio_image_view_ratio_default)
    
    init {
        context.obtainStyledAttributes(attributeSet, R.styleable.FixRatioImageView, 0, 0).use { typedArray ->
            ratioX = typedArray.getInt(R.styleable.FixRatioImageView_ratio_x, defaultRatio)
            ratioY = typedArray.getInt(R.styleable.FixRatioImageView_ratio_y, defaultRatio)
        }
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Missing one of them -> set to auto sizing
        if (ratioX == defaultRatio || ratioY == defaultRatio) {
            return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        val widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)
        
        if (widthMeasureMode == UNSPECIFIED && heightMeasureMode == UNSPECIFIED) {
            // Don't know if the size, let system decide size it self
            return super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        
        var finalWidth: Int
        var finalHeight: Int
        when {
            widthMeasureMode == UNSPECIFIED -> {
                finalHeight = MeasureSpec.getSize(heightMeasureMode)
                finalWidth = finalHeight * ratioX / ratioY
            }
            heightMeasureMode == UNSPECIFIED -> {
                finalWidth = MeasureSpec.getSize(widthMeasureSpec)
                finalHeight = finalWidth * ratioY / ratioX
            }
            else -> {
                val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
                val maxHeight = MeasureSpec.getSize(heightMeasureMode)
                when {
                    ratioX == ratioY -> {
                        val length = min(maxWidth, maxHeight)
                        finalWidth = length
                        finalHeight = length
                    }
                    ratioX > ratioY -> {
                        finalHeight = maxHeight
                        finalWidth = finalHeight * ratioX / ratioY
                        // Adjusting width
                        if (finalWidth > maxWidth) {
                            finalHeight -= maxWidth - finalWidth
                            finalWidth = maxWidth
                        }
                    }
                    else -> {
                        finalWidth = maxWidth
                        finalHeight = finalWidth * ratioY / ratioX
                        // Adjusting height
                        if (finalHeight > maxHeight) {
                            finalWidth -= maxHeight - finalHeight
                            finalHeight = maxHeight
                        }
                    }
                }
            }
        }
        setMeasuredDimension(finalWidth, finalHeight)
    }
    
}