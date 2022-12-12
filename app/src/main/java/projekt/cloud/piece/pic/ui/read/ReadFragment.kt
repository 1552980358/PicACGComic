package projekt.cloud.piece.pic.ui.read

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.ComicDetail
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentReadBinding
import projekt.cloud.piece.pic.ui.read.content.ComicContentFragment

class ReadFragment: BaseFragment<FragmentReadBinding>() {
    
    private companion object {
        const val ARG_ORDER = "order"
    }
    
    private val readComic: ReadComic by viewModels()
    
    private val comicDetail: ComicDetail by activityViewModels()
    
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
        val order = args.getInt(ARG_ORDER)
        readComic.doc = comicDetail.docList.find { it.order == order }
        
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