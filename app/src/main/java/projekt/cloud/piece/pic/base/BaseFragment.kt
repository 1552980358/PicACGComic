package projekt.cloud.piece.pic.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import projekt.cloud.piece.pic.ApplicationConfigs

abstract class BaseFragment<VB: ViewBinding>: Fragment() {
    
    protected val applicationConfigs: ApplicationConfigs by activityViewModels()
    
    private var needInitialSetUp = false
    
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!
    
    protected lateinit var args: Bundle
        private set
    
    protected abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    
    protected open fun setViewModels(binding: VB) = Unit
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        args = requireArguments()
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (_binding == null) {
            _binding = inflateViewBinding(inflater, container)
            setViewModels(binding)
            setUpContainerTransitionName()?.let { binding.root.transitionName = it }
            needInitialSetUp = true
        }
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpToolbar()
        if (needInitialSetUp) {
            setUpViews()
        }
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    
    protected open fun setUpToolbar() = Unit
    
    protected abstract fun setUpViews()
    protected abstract fun setUpContainerTransitionName(): String?
    
    protected inline fun <reified F: Fragment> findParentAs(): F {
        var parent = requireParentFragment()
        while (parent !is F) {
            parent = parent.requireParentFragment()
        }
        return parent
    }
    
}