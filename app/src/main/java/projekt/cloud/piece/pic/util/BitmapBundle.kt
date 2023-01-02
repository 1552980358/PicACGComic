package projekt.cloud.piece.pic.util

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Rect
import android.os.Bundle
import java.io.Serializable
import projekt.cloud.piece.pic.util.ExtraUtil.getSerializableOf

class BitmapBundle: Serializable {
    
    companion object {
        
        private const val BITMAP_BUNDLE_LIB = "bitmap-bundle-lib"
        
        init {
            System.loadLibrary(BITMAP_BUNDLE_LIB)
        }
        
        @JvmStatic
        fun Bundle.putBitmap(name: String, bitmap: Bitmap) = apply {
            val bitmapBundle = BitmapBundle()
            if (bitmapBundle.parseBitmap(bitmap)) {
                putSerializable(name, bitmapBundle)
            }
        }
        
        @JvmStatic
        fun Intent.putBitmap(name: String, bitmap: Bitmap) = apply {
            val bitmapBundle = BitmapBundle()
            if (bitmapBundle.parseBitmap(bitmap)) {
                putExtra(name, bitmapBundle)
            }
        }
        
        @JvmStatic
        fun Intent.getBitmap(name: String): Bitmap? {
            val bitmapBundle = getSerializableOf<BitmapBundle>(name) ?: return null
            return bitmapBundle.recoverBitmap()
        }
        
        @JvmStatic
        fun Bundle.getBitmap(name: String): Bitmap? {
            val bitmapBundle = getSerializableOf<BitmapBundle>(name) ?: return null
            return bitmapBundle.recoverBitmap()
        }
        
        @JvmStatic
        fun Bitmap.toNavArg(): BitmapBundle? {
            return BitmapBundle().takeIf { it.parseBitmap(this) }
        }
        
    }
    
    private var pointer = 0L
    
    private fun recoverBitmap(): Bitmap? {
        val rect = Rect()
        if (!getSize(rect)) {
            return null
        }
        @Suppress("DEPRECATION")
        val config = when (getConfig()) {
            1 -> Config.ARGB_8888
            4 -> Config.RGB_565
            7 -> Config.ARGB_4444
            else -> return null
        }
        return Bitmap.createBitmap(rect.width(), rect.height(), config)
            .takeIf { recoverBitmap(it) }
    }
    
    private external fun parseBitmap(bitmap: Bitmap): Boolean
    
    private external fun getSize(rect: Rect): Boolean
    
    private external fun getConfig(): Int
    
    private external fun recoverBitmap(bitmap: Bitmap): Boolean
    
}