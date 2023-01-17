package projekt.cloud.piece.pic.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import projekt.cloud.piece.pic.R

class GlideShapeableImageView(context: Context, attributeSet: AttributeSet): ShapeableImageView(context, attributeSet) {
    
    companion object GlideShapeableImageViewUtil {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun GlideShapeableImageView.setImageUrl(imageUrl: String?) {
            when (imageUrl) {
                null -> {
                    switchToDefault()
                }
                else -> {
                    Glide.with(this)
                        .load(imageUrl)
                        .into(this)
                }
            }
        }
    }
    
    private val defaultDrawable: Drawable?
    
    init {
        resources.obtainAttributes(attributeSet, R.styleable.GlideShapeableImageView).use { typedArray ->
            defaultDrawable = typedArray.getDrawable(R.styleable.GlideShapeableImageView_defaultImage)
            setImageUrl(typedArray.getString(R.styleable.GlideShapeableImageView_imageUrl))
        }
    }
    
    private fun switchToDefault() {
        setImageDrawable(defaultDrawable)
    }
    
}