package projekt.cloud.piece.pic.ui.home

import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentHomeBinding
import projekt.cloud.piece.pic.util.LayoutUtil.getLayoutSize

class Home: BaseFragment<FragmentHomeBinding>() {

    private lateinit var homeLayoutHelper: HomeLayoutHelper

    override fun onSetupView(binding: FragmentHomeBinding) {
        homeLayoutHelper = HomeLayoutHelper(binding, requireActivity().getLayoutSize())
    }

}