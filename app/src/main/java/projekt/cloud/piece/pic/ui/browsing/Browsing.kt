package projekt.cloud.piece.pic.ui.browsing

import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentBrowsingBinding
import projekt.cloud.piece.pic.ui.browsing.BrowsingLayoutHelper.BrowsingLayoutCompat
import projekt.cloud.piece.pic.ui.browsing.BrowsingLayoutHelper.getLayoutHelper
import projekt.cloud.piece.pic.util.LayoutUtil
import projekt.cloud.piece.pic.util.LayoutUtil.LayoutSizeMode.COMPACT
import projekt.cloud.piece.pic.util.LayoutUtil.getLayoutSize

class Browsing: BaseFragment<FragmentBrowsingBinding>() {

    private lateinit var layoutCompat: BrowsingLayoutCompat

    override fun onSetupAnimation(layoutSizeMode: LayoutUtil.LayoutSizeMode) {
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
        layoutCompat = binding.getLayoutHelper(requireActivity().getLayoutSize())
    }

    override fun onSetupActionBar(binding: FragmentBrowsingBinding) {
        val navController = findNavController()
        layoutCompat.setupActionBar(this, navController)
    }

    override fun onSetupView(binding: FragmentBrowsingBinding) {
        layoutCompat.onSetupInputs()
    }

}