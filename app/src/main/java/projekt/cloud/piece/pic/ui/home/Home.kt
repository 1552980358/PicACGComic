package projekt.cloud.piece.pic.ui.home

import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentHomeBinding
import projekt.cloud.piece.pic.ui.home.HomeLayoutCompat.HomeLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Home: BaseFragment<FragmentHomeBinding>() {

    private lateinit var homeLayoutCompat: HomeLayoutCompat

    override fun onSetupLayoutCompat(binding: FragmentHomeBinding, layoutSizeMode: LayoutSizeMode) {
        homeLayoutCompat = binding.getLayoutCompat(layoutSizeMode)
    }

    override fun onSetupView(binding: FragmentHomeBinding) {
        homeLayoutCompat.setUpNavigationView()
    }

}