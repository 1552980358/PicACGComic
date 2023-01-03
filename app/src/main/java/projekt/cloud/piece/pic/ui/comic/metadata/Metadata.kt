package projekt.cloud.piece.pic.ui.comic.metadata

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelStoreOwner
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseCallbackFragment
import projekt.cloud.piece.pic.databinding.FragmentMetadataBinding
import projekt.cloud.piece.pic.ui.comic.Comic
import projekt.cloud.piece.pic.ui.comic.ComicViewModel
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.MetadataCallbackCode.METADATA_COMPLETE
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.MetadataCallbackCode.METADATA_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.MetadataCallbackCode.METADATA_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.MetadataCallbackCode.METADATA_ERROR
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.MetadataCallbackCode.METADATA_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.MetadataCallbackCode.METADATA_REJECTED
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.MetadataCallbackCode.METADATA_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.comic.metadata.MetadataLayoutCompat.MetadataLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Metadata: BaseCallbackFragment<FragmentMetadataBinding, ComicViewModel>() {
    
    private companion object {
        const val TAG = "Metadata"
    }
    
    private val mainViewModel: MainViewModel by activityViewModels()
    
    private lateinit var layoutCompat: MetadataLayoutCompat
    
    override val viewModelOwner: ViewModelStoreOwner
        get() = findParentAs<Comic>()
    
    override fun onSetupLayoutCompat(binding: FragmentMetadataBinding, layoutSizeMode: LayoutSizeMode) {
        layoutCompat = binding.getLayoutCompat(layoutSizeMode)
    }
    
    override fun onBindData(binding: FragmentMetadataBinding) {
        binding.mainViewModel = mainViewModel
        binding.comicViewModel = viewModel
    }
    
    override fun onSetupActionBar(binding: FragmentMetadataBinding) {
        layoutCompat.setupActionBar(this)
    }
    
    override fun onCallbackReceived(code: Int, message: String?, responseCode: Int?, errorCode: Int?, responseDetail: String?) {
        Log.i(TAG, "onCallbackReceived: code=$code message=$message responseCode=$responseCode errorCode=$errorCode responseDetail=$responseDetail")
        when (code) {
            METADATA_COMPLETE -> {
                // Complete
            }
            METADATA_IO_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_io_exception, message))
            }
            METADATA_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unknown_exception, message))
            }
            METADATA_ERROR -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_error, responseCode, code, message, responseDetail))
            }
            METADATA_EMPTY_CONTENT -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_empty))
            }
            METADATA_REJECTED -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_rejected))
            }
            METADATA_INVALID_STATE_CODE -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unexpected_state, message))
            }
        }
    }
    
}