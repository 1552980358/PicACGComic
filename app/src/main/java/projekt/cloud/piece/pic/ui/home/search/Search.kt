package projekt.cloud.piece.pic.ui.home.search

import androidx.navigation.fragment.findNavController
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentSearchBinding
import projekt.cloud.piece.pic.ui.home.search.SearchLayoutHelper.SearchLayout
import projekt.cloud.piece.pic.ui.home.search.SearchLayoutHelper.getSearchLayout
import projekt.cloud.piece.pic.util.LayoutUtil.getLayoutSize

class Search: BaseFragment<FragmentSearchBinding>() {

    private lateinit var searchLayout: SearchLayout

    override fun onSetupLayoutHelper(binding: FragmentSearchBinding) {
        searchLayout = binding.getSearchLayout(requireActivity().getLayoutSize())
    }

    override fun onSetupActionBar(binding: FragmentSearchBinding) {
        searchLayout.setupSearchBar(this, findNavController())
    }

}