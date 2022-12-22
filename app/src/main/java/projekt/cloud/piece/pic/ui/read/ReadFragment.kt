package projekt.cloud.piece.pic.ui.read

import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentReadBinding
import projekt.cloud.piece.pic.ui.read.content.ComicContentFragment

class ReadFragment: BaseFragment<FragmentReadBinding>() {
    
    private val readComic: ReadComic by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
    }
    
    override fun setUpViews() {
        val order = args.getInt(getString(R.string.read_arg_order))
        readComic.order = order
        
        updateContentFragment()
        readComic.prev.observe(viewLifecycleOwner) {
            updateContentFragment()
        }
        readComic.next.observe(viewLifecycleOwner) {
            updateContentFragment(false)
        }
    }
    
    private fun updateContentFragment(isForward: Boolean = true) {
        val newFragment = ComicContentFragment()
        childFragmentManager.findFragmentById(R.id.fragment_container_view)?.let { currentFragment ->
            currentFragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, isForward)
            newFragment.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, isForward)
        }
        childFragmentManager.commit {
            replace(R.id.fragment_container_view, newFragment)
        }
    }
    
}