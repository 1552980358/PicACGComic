package projekt.cloud.piece.pic.util

import android.graphics.Bitmap
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter

object DataBinding {
    
    @JvmStatic
    @BindingAdapter("bitmap")
    fun AppCompatImageView.setBitmap(bitmap: Bitmap?) {
        setImageBitmap(bitmap)
    }
    
    @JvmStatic
    @BindingAdapter("android:paddingStart")
    fun View.setPaddingStart(paddingStart: Int) {
        setPadding(
            paddingStart,
            paddingTop,
            paddingRight,
            paddingBottom
        )
    }
    
    @JvmStatic
    @BindingAdapter("android:paddingEnd")
    fun View.setPaddingEnd(paddingEnd: Int) {
        setPadding(
            paddingStart,
            paddingTop,
            paddingEnd,
            paddingBottom
        )
    }
    
    @JvmStatic
    @BindingAdapter("android:paddingHorizontal")
    fun View.setPaddingHorizontal(paddingHorizontal: Int) {
        setPadding(
            paddingHorizontal,
            paddingTop,
            paddingHorizontal,
            paddingBottom
        )
    }
    
}