package projekt.cloud.piece.pic.ui.splash

import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentSplashBinding
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Splash: BaseFragment<FragmentSplashBinding>() {

    private val goForward: MaterialButton
        get() = binding.materialButtonGoForward
    
    override fun onSetupAnimation(layoutSizeMode: LayoutSizeMode) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }
    
    override fun onSetupView(binding: FragmentSplashBinding) {
        val navController = findNavController()
        goForward.setOnClickListener {
            navController.navigate(SplashDirections.toBrowsing())
        }
    }
    
}