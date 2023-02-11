package projekt.cloud.piece.pic.ui.signing

import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentSigningBinding
import projekt.cloud.piece.pic.ui.signing.SigningLayoutCompat.SigningLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.util.ScreenDensity
import projekt.cloud.piece.pic.util.ScreenDensity.COMPACT

class Signing: BaseFragment<FragmentSigningBinding>() {

    override fun onSetupAnimation(screenDensity: ScreenDensity) {
        when (screenDensity) {
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
    
    private lateinit var layoutCompat: SigningLayoutCompat
    
    override fun onSetupLayoutCompat(binding: FragmentSigningBinding, screenDensity: ScreenDensity) {
        layoutCompat = binding.getLayoutCompat(screenDensity)
        layoutCompat.setNavController(findNavController())
    }
    
    override fun onSetupActionBar(binding: FragmentSigningBinding) {
        layoutCompat.setupActionBar(this)
    }

}