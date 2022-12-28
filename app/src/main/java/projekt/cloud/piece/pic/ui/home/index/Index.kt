package projekt.cloud.piece.pic.ui.home.index

import androidx.navigation.fragment.findNavController
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentIndexBinding
import projekt.cloud.piece.pic.ui.home.index.IndexLayoutCompat.IndexLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Index: BaseFragment<FragmentIndexBinding>() {

    private lateinit var layoutCompat: IndexLayoutCompat
    
    override fun onSetupLayoutCompat(binding: FragmentIndexBinding, layoutSizeMode: LayoutSizeMode) {
        layoutCompat = binding.getLayoutCompat(layoutSizeMode)
        layoutCompat.setNavController(findNavController())
    }

    override fun onSetupActionBar(binding: FragmentIndexBinding) {
        layoutCompat.setupActionBar(this)
    }

}