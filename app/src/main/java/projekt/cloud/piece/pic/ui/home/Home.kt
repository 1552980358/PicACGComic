package projekt.cloud.piece.pic.ui.home

import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentHomeBinding
import projekt.cloud.piece.pic.util.LayoutUtil.getLayoutSize

class Home: BaseFragment<FragmentHomeBinding>() {

    private lateinit var homeLayoutHelper: HomeLayoutHelper
    private val fragmentContainerView: FragmentContainerView
        get() = binding.fragmentContainerView

    override fun onSetupView(binding: FragmentHomeBinding) {
        val childNavController = fragmentContainerView.getFragment<NavHostFragment>().navController
        homeLayoutHelper = HomeLayoutHelper(binding, requireActivity().getLayoutSize())
        homeLayoutHelper.setUpNavigationView(childNavController)
    }

}