package projekt.cloud.piece.pic.ui.home.index

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseCallbackFragment
import projekt.cloud.piece.pic.databinding.FragmentIndexBinding
import projekt.cloud.piece.pic.ui.home.index.IndexLayoutCompat.IndexLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.ui.home.index.IndexViewModel.IndexViewModelConstants.COLLECTIONS_COMPLETE
import projekt.cloud.piece.pic.ui.home.index.IndexViewModel.IndexViewModelConstants.COLLECTIONS_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.home.index.IndexViewModel.IndexViewModelConstants.COLLECTIONS_ERROR
import projekt.cloud.piece.pic.ui.home.index.IndexViewModel.IndexViewModelConstants.COLLECTIONS_EXCEPTION
import projekt.cloud.piece.pic.ui.home.index.IndexViewModel.IndexViewModelConstants.COLLECTIONS_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.home.index.IndexViewModel.IndexViewModelConstants.COLLECTIONS_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.home.index.IndexViewModel.IndexViewModelConstants.COLLECTIONS_REJECTED
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Index: BaseCallbackFragment<FragmentIndexBinding, IndexViewModel>() {
    
    companion object {
        private const val TAG = "Index"
    }

    private lateinit var layoutCompat: IndexLayoutCompat
    
    private val mainViewModel: MainViewModel by activityViewModels()
    
    override fun onSetupLayoutCompat(binding: FragmentIndexBinding, layoutSizeMode: LayoutSizeMode) {
        layoutCompat = binding.getLayoutCompat(layoutSizeMode)
        layoutCompat.setNavController(findNavController())
    }
    
    override fun onBindData(binding: FragmentIndexBinding) {
        binding.viewModel = viewModel
    }

    override fun onSetupActionBar(binding: FragmentIndexBinding) {
        layoutCompat.setupActionBar(this)
    }
    
    override fun onSetupView(binding: FragmentIndexBinding) {
        layoutCompat.setupRecyclerViews(resources, viewModel.comicListA, viewModel.comicListB, viewModel.coverMap)
        mainViewModel.account.observe(viewLifecycleOwner) {
            Log.i(TAG, "account: $it ${it.isSignedIn}")
            when {
                it.isSignedIn -> obtainCollection(it.token)
                else -> mainViewModel.performSignIn(requireActivity())
            }
        }
        layoutCompat.setupBeforeCompleteLoading(resources)
    }
    
    private fun obtainCollection(token: String) {
        if (!viewModel.isCollectionsObtainComplete) {
            viewModel.scopedObtainCollections(lifecycleScope, token)
        }
    }
    
    override fun onCallbackReceived(code: Int, message: String?, responseCode: Int?, errorCode: Int?, responseDetail: String?) {
        Log.i(TAG, "onCallbackReceived: code=$code message=$message responseCode=$responseCode errorCode=$errorCode responseDetail=$responseDetail")
        when (code) {
            COLLECTIONS_COMPLETE -> {
                // Complete
                layoutCompat.notifyUpdate()
                layoutCompat.completeLoading()
            }
            COLLECTIONS_IO_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_io_exception, message))
            }
            COLLECTIONS_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unknown_exception, message))
            }
            COLLECTIONS_ERROR -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_error, responseCode, code, message, responseDetail))
            }
            COLLECTIONS_EMPTY_CONTENT -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_empty))
            }
            COLLECTIONS_REJECTED -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_rejected))
            }
            COLLECTIONS_INVALID_STATE_CODE -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unexpected_state, message))
            }
        }
    }

}