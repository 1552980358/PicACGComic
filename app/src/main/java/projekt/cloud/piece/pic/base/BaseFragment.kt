package projekt.cloud.piece.pic.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import projekt.cloud.piece.pic.ApplicationConfigs
import projekt.cloud.piece.pic.R

abstract class BaseFragment<VB: ViewBinding>: Fragment() {
    
    private companion object {
        const val VIEW_BINDING_INFLATE_METHOD_NAME = "inflate"
    }
    
    protected val applicationConfigs: ApplicationConfigs by activityViewModels()
    
    private var needInitialSetUp = false
    
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!
    
    protected val args by lazy { requireArguments() }
    
    @Suppress("UNCHECKED_CAST")
    private val viewBindingClass =
        ((this::class.java.genericSuperclass as ParameterizedType)
            .actualTypeArguments.first() as Class<VB>)
    
    private val viewBindingInflateMethod =
        viewBindingClass.getDeclaredMethod(
            VIEW_BINDING_INFLATE_METHOD_NAME,
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
    
    @Suppress("UNCHECKED_CAST")
    protected fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        viewBindingInflateMethod.invoke(null, inflater, container, false) as VB
    
    protected open fun setViewModels(binding: VB) = Unit
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        requireActivity().onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (onBackPressed()) {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        })
        
        val requestKey = getString(R.string.fragment_message)
        setFragmentResultListener(requestKey) { _, bundle ->
            clearFragmentResultListener(getString(R.string.fragment_message))
            
            when (bundle.getString(getString(R.string.fragment_message_sender))) {
                this::class.simpleName -> bundle.getString(getString(R.string.fragment_message_message))?.let {
                    onMessageReceived(it)
                }
                else -> setFragmentResult(requestKey, bundle)
            }
        }
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflateViewBinding(inflater, container)
        val binding = binding
        if (binding is ViewDataBinding) {
            binding.lifecycleOwner = this
            setViewModels(binding)
        }
        return binding.root.apply { containerTransitionName?.let { transitionName = it } }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpToolbar()
        setUpViews()
    }
    
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    
    protected open fun onBackPressed(): Boolean {
        return true
    }
    
    protected open fun setUpToolbar() = Unit
    
    protected abstract fun setUpViews()
    protected open val containerTransitionName: String?
        get() = null
    
    protected inline fun <reified F: Fragment> findParentAs(): F {
        var parent = requireParentFragment()
        while (parent !is F) {
            parent = parent.requireParentFragment()
        }
        return parent
    }
    
    open fun onMessageReceived(message: String) = Unit
    
    fun sendMessage(message: String) {
        val resultKey = getString(R.string.fragment_message)
        clearFragmentResultListener(resultKey)
        setFragmentResult(
            resultKey,
            bundleOf(
                getString(R.string.fragment_message_sender) to this::class.simpleName,
                getString(R.string.fragment_message_message) to message
            )
        )
    }
    
}