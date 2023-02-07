package projekt.cloud.piece.pic.ui.comic.metadata

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseCallbackFragment
import projekt.cloud.piece.pic.databinding.FragmentMetadataBinding
import projekt.cloud.piece.pic.ui.comic.Comic
import projekt.cloud.piece.pic.ui.comic.ComicViewModel
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_COMPLETE
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_ERROR
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_REJECTED
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_ERROR
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_REJECTED
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_COMPLETE
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_ERROR
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_REJECTED
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.comic.metadata.MetadataLayoutCompat.MetadataLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.util.FragmentUtil.findParentAs
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Metadata: BaseCallbackFragment<FragmentMetadataBinding, ComicViewModel>() {
    
    private val comic: Comic
        get() = findParentAs()
    
    private companion object {
        const val TAG = "Metadata"
    }
    
    private val mainViewModel: MainViewModel by activityViewModels()
    
    private lateinit var layoutCompat: MetadataLayoutCompat
    
    override val viewModelOwner: ViewModelStoreOwner
        get() = findParentAs<Comic>()
    
    override fun onSetupLayoutCompat(binding: FragmentMetadataBinding, layoutSizeMode: LayoutSizeMode) {
        layoutCompat = binding.getLayoutCompat(layoutSizeMode)
        layoutCompat.setNavController(comic.findNavController())
    }
    
    override fun onBindData(binding: FragmentMetadataBinding) {
        binding.mainViewModel = mainViewModel
        binding.comicViewModel = viewModel
        viewModel.comic.observe(viewLifecycleOwner) {
            it?.let {
                layoutCompat.startLoadAvatar(this, it.creator.avatar)
                layoutCompat.startUpdateCategoryAndTag(lifecycleScope, requireContext(), it.categoryList, it.tagList)
            }
        }
    }
    
    override fun onSetupActionBar(binding: FragmentMetadataBinding) {
        layoutCompat.setupActionBar(this)
        layoutCompat.setupMenu(this, mainViewModel, viewModel)
    }
    
    override fun onSetupView(binding: FragmentMetadataBinding) {
        layoutCompat.setupRecyclerView(viewModel.episodeList)
    }
    
    override fun onCallbackReceived(code: Int, message: String?, responseCode: Int?, errorCode: Int?, responseDetail: String?) {
        Log.i(TAG, "onCallbackReceived: code=$code message=$message responseCode=$responseCode errorCode=$errorCode responseDetail=$responseDetail")
        when (code) {
            METADATA_COMPLETE -> {
                // Complete
            }
            EPISODES_COMPLETE -> {
                layoutCompat.notifyUpdate()
            }
            METADATA_IO_EXCEPTION, EPISODES_IO_EXCEPTION, FAVOURITE_IO_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_io_exception, message))
            }
            METADATA_EXCEPTION, EPISODES_EXCEPTION, FAVOURITE_EXCEPTION -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unknown_exception, message))
            }
            METADATA_ERROR, EPISODES_ERROR, FAVOURITE_ERROR -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_error, responseCode, code, message, responseDetail))
            }
            METADATA_EMPTY_CONTENT, EPISODES_EMPTY_CONTENT, FAVOURITE_EMPTY_CONTENT -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_empty))
            }
            METADATA_REJECTED, EPISODES_REJECTED, FAVOURITE_REJECTED -> {
                layoutCompat.indefiniteSnack(getString(R.string.response_rejected))
            }
            METADATA_INVALID_STATE_CODE, EPISODES_INVALID_STATE_CODE, FAVOURITE_INVALID_STATE_CODE -> {
                layoutCompat.indefiniteSnack(getString(R.string.request_unexpected_state, message))
            }
        }
    }
    
}