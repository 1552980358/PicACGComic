package projekt.cloud.piece.pic.ui.home

import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentHomeBinding
import projekt.cloud.piece.pic.ui.home.HomeLayoutCompat.HomeLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.util.ScreenDensity

class Home: BaseFragment<FragmentHomeBinding>() {

    private lateinit var homeLayoutCompat: HomeLayoutCompat
    
    override fun onSetupAnimation(screenDensity: ScreenDensity) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onSetupLayoutCompat(binding: FragmentHomeBinding, screenDensity: ScreenDensity) {
        homeLayoutCompat = binding.getLayoutCompat(screenDensity)
    }

    override fun onSetupView(binding: FragmentHomeBinding) {
        homeLayoutCompat.setUpNavigationView()
    }

}