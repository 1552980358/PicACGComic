package projekt.cloud.piece.pic.ui.splash

import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentSplashBinding

class Splash: BaseFragment<FragmentSplashBinding>() {

    private val goForward: MaterialButton
        get() = binding.materialButtonGoForward
    
    override fun onSetupView(binding: FragmentSplashBinding) {
        val navController = findNavController()
        goForward.setOnClickListener {
            navController.navigate(SplashDirections.toBrowsing())
        }
    }
    
}