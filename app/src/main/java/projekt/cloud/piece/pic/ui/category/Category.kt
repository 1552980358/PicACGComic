package projekt.cloud.piece.pic.ui.category

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseCallbackFragment
import projekt.cloud.piece.pic.databinding.FragmentCategoryBinding
import projekt.cloud.piece.pic.ui.category.CategoryLayoutCompat.CategoryLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.ui.category.CategoryViewModel.CategoryViewModelUtil.CATEGORY_COMPLETE
import projekt.cloud.piece.pic.ui.category.CategoryViewModel.CategoryViewModelUtil.CATEGORY_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.category.CategoryViewModel.CategoryViewModelUtil.CATEGORY_ERROR
import projekt.cloud.piece.pic.ui.category.CategoryViewModel.CategoryViewModelUtil.CATEGORY_EXCEPTION
import projekt.cloud.piece.pic.ui.category.CategoryViewModel.CategoryViewModelUtil.CATEGORY_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.category.CategoryViewModel.CategoryViewModelUtil.CATEGORY_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.category.CategoryViewModel.CategoryViewModelUtil.CATEGORY_REJECTED
import projekt.cloud.piece.pic.ui.category.CategoryViewModel.CategoryViewModelUtil.CATEGORY_UPDATED
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Category: BaseCallbackFragment<FragmentCategoryBinding, CategoryViewModel>() {
    
    private companion object {
        const val TAG = "Category"
    }
    
    private val mainViewModel: MainViewModel by activityViewModels()
    
    override fun onSetupAnimation(layoutSizeMode: LayoutSizeMode) {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }
    
    private lateinit var layoutCompat: CategoryLayoutCompat
    
    override fun onBindData(binding: FragmentCategoryBinding) {
        binding.mainViewModel = mainViewModel
        binding.categoryViewModel = viewModel
    }
    
    override fun onSetupLayoutCompat(binding: FragmentCategoryBinding, layoutSizeMode: LayoutSizeMode) {
        layoutCompat = binding.getLayoutCompat(layoutSizeMode)
        layoutCompat.setNavController(this)
    }
    
    override fun onSetupActionBar(binding: FragmentCategoryBinding) {
        layoutCompat.setupActionBar(this)
    }
    
    override fun onSetupView(binding: FragmentCategoryBinding) {
        layoutCompat.setupRecyclerView(this, viewModel.comicList)
        mainViewModel.account.observe(viewLifecycleOwner) {
            val title = requireArguments().getString(getString(R.string.category_title))
            if (!title.isNullOrBlank()) {
                when {
                    it.isSignedIn -> viewModel.scopedObtainCategoryComics(it.token, title, lifecycleScope)
                    else -> mainViewModel.performSignIn(requireActivity())
                }
            }
        }
    }
    
    override fun onCallbackReceived(code: Int, message: String?, responseCode: Int?, errorCode: Int?, responseDetail: String?) {
        Log.i(TAG, "onCallbackReceived: code=$code message=$message responseCode=$responseCode errorCode=$errorCode responseDetail=$responseDetail")
        when (code) {
            CATEGORY_UPDATED -> {
                layoutCompat.notifyUpdate()
            }
            CATEGORY_COMPLETE -> {
                // Complete
            }
            CATEGORY_IO_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_io_exception, message))
            }
            CATEGORY_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unknown_exception, message))
            }
            CATEGORY_ERROR -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_error, responseCode, code, message, responseDetail))
            }
            CATEGORY_EMPTY_CONTENT -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_empty))
            }
            CATEGORY_REJECTED -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_rejected))
            }
            CATEGORY_INVALID_STATE_CODE -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unexpected_state, message))
            }
        }
    }

}