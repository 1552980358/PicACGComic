package projekt.cloud.piece.pic.ui.read

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentReadBinding
import projekt.cloud.piece.pic.ui.read.content.ComicContentFragment

class ReadFragment: BaseFragment<FragmentReadBinding>() {
    
    private val readComic: ReadComic by viewModels()
    
    private var currentContentFragment: Fragment? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
    }
    
    override val containerTransitionName: String?
        get() {
            val readTransition = getString(R.string.read_transition)
            return when {
                args.containsKey(readTransition) -> args.getString(readTransition)
                else -> readTransition
            }
        }
    
    override fun setUpViews() {
        updateContentFragment(requireAnimation = false)
        readComic.prev.observe(viewLifecycleOwner) {
            updateContentFragment()
        }
        readComic.next.observe(viewLifecycleOwner) {
            updateContentFragment(isForward = false)
        }
    }
    
    private fun updateContentFragment(fragment: Fragment = ComicContentFragment(),
                                      requireAnimation: Boolean = true,
                                      isForward: Boolean = true) {
        if (requireAnimation) {
            currentContentFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, isForward)
            fragment.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, isForward)
        }
        childFragmentManager.commit {
            replace(R.id.fragment_container_view, fragment)
        }
        currentContentFragment = fragment
    }
    
    
    
}