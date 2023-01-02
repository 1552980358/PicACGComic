package projekt.cloud.piece.pic.ui.home

import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentHomeBinding
import projekt.cloud.piece.pic.ui.home.HomeLayoutCompat.HomeLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Home: BaseFragment<FragmentHomeBinding>() {

    private lateinit var homeLayoutCompat: HomeLayoutCompat
    
    override fun onSetupAnimation(layoutSizeMode: LayoutSizeMode) {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }

    override fun onSetupLayoutCompat(binding: FragmentHomeBinding, layoutSizeMode: LayoutSizeMode) {
        homeLayoutCompat = binding.getLayoutCompat(layoutSizeMode)
    }

    override fun onSetupView(binding: FragmentHomeBinding) {
        homeLayoutCompat.setUpNavigationView()
    }

}