package projekt.cloud.piece.pic.ui.home.search

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.base.BaseCallbackFragment
import projekt.cloud.piece.pic.databinding.FragmentSearchBinding
import projekt.cloud.piece.pic.ui.home.Home
import projekt.cloud.piece.pic.ui.home.search.SearchLayoutCompact.SearchLayoutCompactUtil.getLayoutCompat
import projekt.cloud.piece.pic.ui.home.search.SearchViewModel.SearchViewModelUtil.SEARCH_COMPLETE
import projekt.cloud.piece.pic.ui.home.search.SearchViewModel.SearchViewModelUtil.SEARCH_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.home.search.SearchViewModel.SearchViewModelUtil.SEARCH_ERROR
import projekt.cloud.piece.pic.ui.home.search.SearchViewModel.SearchViewModelUtil.SEARCH_EXCEPTION
import projekt.cloud.piece.pic.ui.home.search.SearchViewModel.SearchViewModelUtil.SEARCH_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.home.search.SearchViewModel.SearchViewModelUtil.SEARCH_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.home.search.SearchViewModel.SearchViewModelUtil.SEARCH_PAGE_UPDATE
import projekt.cloud.piece.pic.ui.home.search.SearchViewModel.SearchViewModelUtil.SEARCH_REJECTED
import projekt.cloud.piece.pic.util.FragmentUtil.findParentAs
import projekt.cloud.piece.pic.util.ScreenDensity

class Search: BaseCallbackFragment<FragmentSearchBinding, SearchViewModel>() {
    
    private val home: Home
        get() = findParentAs()
    
    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var layoutCompat: SearchLayoutCompact
    
    override fun onBindData(binding: FragmentSearchBinding) {
        binding.searchViewModel = viewModel
        binding.mainViewModel = mainViewModel
    }

    override fun onSetupLayoutCompat(binding: FragmentSearchBinding, screenDensity: ScreenDensity) {
        layoutCompat = binding.getLayoutCompat(screenDensity)
    }

    override fun onSetupActionBar(binding: FragmentSearchBinding) {
        layoutCompat.setupSearchBar(this, mainViewModel, viewModel)
    }
    
    override fun onSetupView(binding: FragmentSearchBinding) {
        layoutCompat.setNavController(home.findNavController())
        layoutCompat.setupRecyclerView(viewModel.comicList, this)
    }
    
    override fun onCallbackReceived(code: Int, message: String?, responseCode: Int?, errorCode: Int?, responseDetail: String?) {
        when (code) {
            SEARCH_PAGE_UPDATE -> {
                layoutCompat.notifyUpdate()
            }
            SEARCH_COMPLETE -> {
            
            }
            SEARCH_IO_EXCEPTION -> {
            
            }
            SEARCH_EXCEPTION -> {
            
            }
            SEARCH_INVALID_STATE_CODE -> {
            
            }
            SEARCH_ERROR -> {
            
            }
            SEARCH_EMPTY_CONTENT -> {
            
            }
            SEARCH_REJECTED -> {
            
            }
        }
    }

}