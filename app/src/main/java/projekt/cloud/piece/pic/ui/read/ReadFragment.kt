package projekt.cloud.piece.pic.ui.read

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialSharedAxis
import projekt.cloud.piece.pic.ComicDetail
import projekt.cloud.piece.pic.R
import projekt.cloud.piece.pic.base.BaseFragment
import projekt.cloud.piece.pic.databinding.FragmentReadBinding
import projekt.cloud.piece.pic.ui.read.content.ComicContentFragment

class ReadFragment: BaseFragment<FragmentReadBinding>() {
    
    private val readComic: ReadComic by viewModels()
    
    private val comicDetail: ComicDetail by activityViewModels()
    
    private var requireUpdateOrder = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform()
    }
    
    override val containerTransitionName: String?
        get() {
            val readTransition = getString(R.string.read_transition)
            return when {
                args.containsKey(readTransition) -> {
                    requireUpdateOrder = true
                    args.getString(readTransition)
                }
                else -> readTransition
            }
        }
    
    override fun setUpViews() {
        val order = args.getInt(getString(R.string.read_transition_order))
        readComic.doc = comicDetail.docList.find { it.order == order }
        updateOrder(order)
        
        updateContentFragment()
        readComic.prev.observe(viewLifecycleOwner) {
            updateContentFragment()
            updateOrder(it.order)
        }
        readComic.next.observe(viewLifecycleOwner) {
            updateContentFragment(false)
            updateOrder(it.order)
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
    
    private fun updateOrder(newOrder: Int) {
        val order = getString(R.string.read_transition_order)
        setFragmentResult(order, bundleOf(order to newOrder))
    }
    
}