package projekt.cloud.piece.pic.ui.viewer

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseCallbackFragment
import projekt.cloud.piece.pic.databinding.FragmentViewerBinding
import projekt.cloud.piece.pic.ui.viewer.ViewerLayoutCompat.ViewerLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.ui.viewer.ViewerViewModel.ViewerViewModelCallbackCode.VIEWER_COMPLETE
import projekt.cloud.piece.pic.ui.viewer.ViewerViewModel.ViewerViewModelCallbackCode.VIEWER_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.viewer.ViewerViewModel.ViewerViewModelCallbackCode.VIEWER_ERROR
import projekt.cloud.piece.pic.ui.viewer.ViewerViewModel.ViewerViewModelCallbackCode.VIEWER_EXCEPTION
import projekt.cloud.piece.pic.ui.viewer.ViewerViewModel.ViewerViewModelCallbackCode.VIEWER_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.viewer.ViewerViewModel.ViewerViewModelCallbackCode.VIEWER_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.viewer.ViewerViewModel.ViewerViewModelCallbackCode.VIEWER_REJECTED
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Viewer: BaseCallbackFragment<FragmentViewerBinding, ViewerViewModel>() {
    
    private val mainViewModel: MainViewModel by activityViewModels()
    
    private lateinit var layoutCompat: ViewerLayoutCompat
    
    override fun onBindData(binding: FragmentViewerBinding) {
        binding.mainViewModel = mainViewModel
        binding.viewerViewModel = viewModel
        
        val argument = requireArguments()
        val id = argument.getString(getString(R.string.viewer_arg_id))
        if (id != null) {
            val order = argument.getInt(getString(R.string.viewer_arg_order))
            
            if (viewModel.id.value != id || order != viewModel.order.value) {
                viewModel.setId(id)
                viewModel.setTitle(argument.getString(getString(R.string.viewer_arg_title)))
                viewModel.setOrder(order)
                viewModel.setLastOrder(argument.getInt(getString(R.string.viewer_arg_last_order)))
                mainViewModel.account.observe(viewLifecycleOwner) {
                    when {
                        it.isSignedIn -> {
                            startRequestComicImages(it.token, id, order)
                        }
                        else -> {
                            mainViewModel.performSignIn(requireActivity())
                        }
                    }
                }
            }
        }
        
    }
    
    override fun onSetupLayoutCompat(binding: FragmentViewerBinding, layoutSizeMode: LayoutSizeMode) {
        layoutCompat = binding.getLayoutCompat(layoutSizeMode)
    }
    
    override fun onSetupActionBar(binding: FragmentViewerBinding) {
        layoutCompat.setupActionBar(this)
    }
    
    override fun onSetupView(binding: FragmentViewerBinding) {
        layoutCompat.setupRecyclerView(this, viewModel.orderValue, viewModel.lastOrderValue, viewModel.episodeImageList)
    }
    
    private fun startRequestComicImages(token: String, id: String, order: Int) {
        viewModel.scopedRequestComicImages(token, id, order, lifecycleScope)
    }
    
    override fun onCallbackReceived(code: Int, message: String?, responseCode: Int?, errorCode: Int?, responseDetail: String?) {
        when (code) {
            VIEWER_COMPLETE -> {
                layoutCompat.notifyUpdate()
            }
            VIEWER_IO_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_io_exception, message))
            }
            VIEWER_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unknown_exception, message))
            }
            VIEWER_ERROR -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_error, responseCode, code, message, responseDetail))
            }
            VIEWER_EMPTY_CONTENT -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_empty))
            }
            VIEWER_REJECTED -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_rejected))
            }
            VIEWER_INVALID_STATE_CODE -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unexpected_state, message))
            }
        }
    }

}