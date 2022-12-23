package projekt.cloud.piece.pic.ui.splash

import androidx.navigation.fragment.findNavController
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentSplashBinding
import projekt.cloud.piece.pic.ui.splash.SplashLayoutHelper.SplashLayout
import projekt.cloud.piece.pic.ui.splash.SplashLayoutHelper.getSplashLayout
import projekt.cloud.piece.pic.util.LayoutUtil.getLayoutSize

class Splash: BaseFragment<FragmentSplashBinding>() {
    
    private lateinit var splashLayout: SplashLayout
    
    override fun onSetupLayoutHelper(binding: FragmentSplashBinding) {
        splashLayout = binding.getSplashLayout(requireActivity().getLayoutSize())
    }
    
    override fun onSetupActionBar(binding: FragmentSplashBinding) {
        splashLayout.setupToolbar(this, findNavController())
    }
    
}