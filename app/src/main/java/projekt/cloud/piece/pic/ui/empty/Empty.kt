package projekt.cloud.piece.pic.ui.empty

import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentEmptyBinding
import projekt.cloud.piece.pic.util.ScreenDensity

class Empty: BaseFragment<FragmentEmptyBinding>() {
    
    override fun onSetupAnimation(screenDensity: ScreenDensity) {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
    }
    
}