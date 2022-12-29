package projekt.cloud.piece.pic.util

import android.graphics.Bitmap
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter

object DataBinding {
    
    @JvmStatic
    @BindingAdapter("bitmap")
    fun AppCompatImageView.setBitmap(bitmap: Bitmap?) {
        setImageBitmap(bitmap)
    }
    
}