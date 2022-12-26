package projekt.cloud.piece.pic.ui.browsing

import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentBrowsingBinding
import projekt.cloud.piece.pic.ui.browsing.BrowsingLayoutCompat.BrowsingLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.util.LayoutSizeMode
import projekt.cloud.piece.pic.util.LayoutSizeMode.COMPACT

class Browsing: BaseFragment<FragmentBrowsingBinding>() {

    private lateinit var layoutCompat: BrowsingLayoutCompat

    override fun onSetupAnimation(layoutSizeMode: LayoutSizeMode) {
        when (layoutSizeMode) {
            COMPACT -> {
                enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
            }
            else -> {
                enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
                exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true)
                reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false)
            }
        }
    }

    override fun onSetupLayoutHelper(binding: FragmentBrowsingBinding) {
        layoutCompat = binding.getLayoutCompat(requireActivity())
        layoutCompat.setNavController(findNavController())
    }

    override fun onSetupActionBar(binding: FragmentBrowsingBinding) {
        layoutCompat.setupActionBar(this)
    }

    override fun onSetupView(binding: FragmentBrowsingBinding) {
        layoutCompat.onSetupInputs()
    }

}