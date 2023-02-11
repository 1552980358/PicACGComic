package projekt.cloud.piece.pic.ui.home.categories

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseCallbackFragment
import projekt.cloud.piece.pic.databinding.FragmentCategoriesBinding
import projekt.cloud.piece.pic.ui.home.Home
import projekt.cloud.piece.pic.ui.home.categories.CategoriesLayoutCompat.CategoriesLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.ui.home.categories.CategoriesViewModel.CategoriesViewModelUtil.CATEGORIES_COMPLETE
import projekt.cloud.piece.pic.ui.home.categories.CategoriesViewModel.CategoriesViewModelUtil.CATEGORIES_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.home.categories.CategoriesViewModel.CategoriesViewModelUtil.CATEGORIES_ERROR
import projekt.cloud.piece.pic.ui.home.categories.CategoriesViewModel.CategoriesViewModelUtil.CATEGORIES_EXCEPTION
import projekt.cloud.piece.pic.ui.home.categories.CategoriesViewModel.CategoriesViewModelUtil.CATEGORIES_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.home.categories.CategoriesViewModel.CategoriesViewModelUtil.CATEGORIES_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.home.categories.CategoriesViewModel.CategoriesViewModelUtil.CATEGORIES_REJECTED
import projekt.cloud.piece.pic.util.FragmentUtil.findParentAs
import projekt.cloud.piece.pic.util.ScreenDensity

class Categories: BaseCallbackFragment<FragmentCategoriesBinding, CategoriesViewModel>() {
    
    private companion object {
        const val TAG = "Categories"
    }
    
    private lateinit var layoutCompat: CategoriesLayoutCompat
    
    private val mainViewModel: MainViewModel by activityViewModels()
    
    private val home: Home
        get() = findParentAs()
    
    override fun onBindData(binding: FragmentCategoriesBinding) {
        binding.mainViewModel = mainViewModel
    }
    
    override fun onSetupLayoutCompat(binding: FragmentCategoriesBinding, screenDensity: ScreenDensity) {
        layoutCompat = binding.getLayoutCompat(screenDensity)
        layoutCompat.setNavController(home.findNavController())
    }
    
    override fun onSetupView(binding: FragmentCategoriesBinding) {
        layoutCompat.setupRecyclerView(viewModel.categoryList, this, resources)
        mainViewModel.account.observe(viewLifecycleOwner) {
            when {
                it.isSignedIn -> viewModel.scopedObtainCategories(it.token, lifecycleScope)
                else -> mainViewModel.performSignIn(requireActivity())
            }
        }
    }
    
    override fun onSetupActionBar(binding: FragmentCategoriesBinding) {
        layoutCompat.setupActionBar(this, home)
    }
    
    override fun onCallbackReceived(code: Int, message: String?, responseCode: Int?, errorCode: Int?, responseDetail: String?) {
        Log.i(TAG, "onCallbackReceived: code=$code message=$message responseCode=$responseCode errorCode=$errorCode responseDetail=$responseDetail")
        when (code) {
            CATEGORIES_COMPLETE -> {
                // Complete
                layoutCompat.notifyUpdate()
            }
            CATEGORIES_IO_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_io_exception, message))
            }
            CATEGORIES_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unknown_exception, message))
            }
            CATEGORIES_ERROR -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_error, responseCode, code, message, responseDetail))
            }
            CATEGORIES_EMPTY_CONTENT -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_empty))
            }
            CATEGORIES_REJECTED -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_rejected))
            }
            CATEGORIES_INVALID_STATE_CODE -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unexpected_state, message))
            }
        }
    }

}