package projekt.cloud.piece.pic.ui.comic.metadata

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import projekt.cloud.piece.pic.base.SnackLayoutCompat
import projekt.cloud.piece.pic.databinding.FragmentMetadataBinding
import projekt.cloud.piece.pic.util.FragmentUtil.setSupportActionBar
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutSizeMode.MEDIUM
import projekt.cloud.piece.pic.util.LayoutSizeMode.EXPANDED

abstract class MetadataLayoutCompat(protected val binding: FragmentMetadataBinding): SnackLayoutCompat() {

    companion object MetadataLayoutCompatUtil {
        @JvmStatic
        fun FragmentMetadataBinding.getLayoutCompat(layoutSizeMode: LayoutSizeMode) = when (layoutSizeMode) {
            COMPACT -> MetadataLayoutCompatImpl(this)
            MEDIUM -> MetadataLayoutCompatW600dpImpl(this)
            EXPANDED -> MetadataLayoutCompatW1240dpImpl(this)
        }
    }
    
    open fun setupActionBar(fragment: Fragment) = Unit
    
    override val snackContainer: View
        get() = binding.root
    
    private class MetadataLayoutCompatImpl(binding: FragmentMetadataBinding): MetadataLayoutCompat(binding) {
    
        private val toolbar: MaterialToolbar
            get() = binding.materialToolbar!!
        
        override fun setupActionBar(fragment: Fragment) {
            fragment.setSupportActionBar(toolbar)
        }
        
    }
    
    private class MetadataLayoutCompatW600dpImpl(binding: FragmentMetadataBinding): MetadataLayoutCompat(binding) {
        
        private val toolbar: MaterialToolbar
            get() = binding.materialToolbar!!
    
        override fun setupActionBar(fragment: Fragment) {
            fragment.setSupportActionBar(toolbar)
        }
        
    }
    
    private class MetadataLayoutCompatW1240dpImpl(binding: FragmentMetadataBinding): MetadataLayoutCompat(binding) {
    
    }

}