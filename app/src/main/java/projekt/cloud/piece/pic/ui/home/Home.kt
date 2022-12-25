package projekt.cloud.piece.pic.ui.home

import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentHomeBinding
import projekt.cloud.piece.pic.ui.home.HomeLayoutCompat.HomeLayoutCompatUtil.getLayoutCompat

class Home: BaseFragment<FragmentHomeBinding>() {

    private lateinit var homeLayoutCompat: HomeLayoutCompat

    override fun onSetupLayoutHelper(binding: FragmentHomeBinding) {
        homeLayoutCompat = binding.getLayoutCompat(requireActivity())
    }

    override fun onSetupView(binding: FragmentHomeBinding) {
        homeLayoutCompat.setUpNavigationView()
    }

}