package projekt.cloud.piece.pic.ui.home.search

import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentSearchBinding
import projekt.cloud.piece.pic.ui.home.search.SearchLayoutCompact.SearchLayoutCompactUtil.getLayoutCompat
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Search: BaseFragment<FragmentSearchBinding>() {

    private lateinit var layoutCompat: SearchLayoutCompact

    override fun onSetupLayoutCompat(binding: FragmentSearchBinding, layoutSizeMode: LayoutSizeMode) {
        layoutCompat = binding.getLayoutCompat(layoutSizeMode)
    }

    override fun onSetupActionBar(binding: FragmentSearchBinding) {
        layoutCompat.setupSearchBar(this)
    }

}