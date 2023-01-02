package projekt.cloud.piece.pic.ui.comic

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.MainViewModel
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentComicBinding
import projekt.cloud.piece.pic.ui.comic.ComicLayoutCompat.ComicLayoutCompatUtil.getLayoutCompat
import projekt.cloud.piece.pic.util.BitmapBundle.Companion.getBitmap
import projekt.cloud.piece.pic.util.LayoutSizeMode

class Comic: BaseFragment<FragmentComicBinding>() {
    
    private val mainViewModel: MainViewModel by activityViewModels()
    
    private lateinit var layoutCompat: ComicLayoutCompat
    
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
        
        val comicViewModel = ViewModelProvider(
            layoutCompat.childNavController.getViewModelStoreOwner(R.id.nav_graph_comic)
        )[ComicViewModel::class.java]
        
        with(comicViewModel) {
            val arguments = requireArguments()
            setCover(arguments.getBitmap(getString(R.string.comic_arg_cover)))
            setId(arguments.getString(getString(R.string.comic_arg_id)))
            setTitle(arguments.getString(getString(R.string.comic_arg_title)))
        }
        
    }

}