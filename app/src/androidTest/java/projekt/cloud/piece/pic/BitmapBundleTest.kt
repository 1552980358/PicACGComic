package projekt.cloud.piece.pic

import android.graphics.Bitmap
import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import projekt.cloud.piece.pic.util.BitmapBundle.Companion.getBitmap
import projekt.cloud.piece.pic.util.BitmapBundle.Companion.putBitmap

@RunWith(AndroidJUnit4::class)
class BitmapBundleTest {
    
    @Test
    fun bundleParsing() {
        val bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        val bundle = bundleOf().putBitmap("b", bitmap)
        val out = bundle.getBitmap("b")
        assertEquals(bitmap.config, out?.config)
    }
    
}