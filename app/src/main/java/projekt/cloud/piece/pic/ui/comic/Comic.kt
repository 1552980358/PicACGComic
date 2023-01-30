package projekt.cloud.piece.pic.ui.comic

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseCallbackFragment
import projekt.cloud.piece.pic.databinding.FragmentComicBinding
import projekt.cloud.piece.pic.ui.comic.ComicLayoutCompat.ComicLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_COMPLETE
import projekt.cloud.piece.pic.util.BitmapBundle.Companion.getBitmapAndClose
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Comic: BaseCallbackFragment<FragmentComicBinding, ComicViewModel>() {
    
    private val mainViewModel: MainViewModel by activityViewModels()
    
    private lateinit var layoutCompat: ComicLayoutCompat
    
    private lateinit var childNavController: NavController
    
    override fun onSetupAnimation(layoutSizeMode: LayoutSizeMode) {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }
    
    override fun onBindData(binding: FragmentComicBinding) {
        binding.mainViewModel = mainViewModel
    }
    
    override fun onSetupLayoutCompat(binding: FragmentComicBinding, layoutSizeMode: LayoutSizeMode) {
        layoutCompat = binding.getLayoutCompat(layoutSizeMode)
        layoutCompat.setNavController(findNavController())
        layoutCompat.setupNavigation(this)
        layoutCompat.setupHeader(viewModel, mainViewModel, viewLifecycleOwner)
    }
    
    override fun onSetupView(binding: FragmentComicBinding) {
        val arguments = requireArguments()
        val id = arguments.getString(getString(R.string.comic_arg_id))
        if (id != null && viewModel.id.value != id) {
            viewModel.setId(id)
            viewModel.setCover(arguments.getBitmapAndClose(getString(R.string.comic_arg_cover)))
            viewModel.setTitle(arguments.getString(getString(R.string.comic_arg_title)))
            mainViewModel.account.observe(viewLifecycleOwner) {
                when {
                    it.isSignedIn -> startRequestComicMetadata(it.token, id)
                    else -> mainViewModel.performSignIn(requireActivity())
                }
            }
        }
    }

    private fun startRequestComicMetadata(token: String, id: String) {
        viewModel.scopedRequestComicData(lifecycleScope, token, id)
    }
    
    override fun onCallbackReceived(code: Int, message: String?, responseCode: Int?, errorCode: Int?, responseDetail: String?) {
        if (code == METADATA_COMPLETE) {
            viewModel.comic.value?.let {
                when {
                    !it.allowComment -> layoutCompat.disableComment()
                    else -> layoutCompat.updateCommentCount(it.commentsCount)
                }
            }
        }
    }
    
    override fun onBackPressed(): Boolean {
        return layoutCompat.onBackPressed()
    }
    
}