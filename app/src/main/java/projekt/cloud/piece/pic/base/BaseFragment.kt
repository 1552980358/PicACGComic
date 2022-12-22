package projekt.cloud.piece.pic.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VB: ViewBinding>: Fragment() {

    private companion object {
        const val VIEW_BINDING_INFLATE_METHOD_NAME = "inflate"
    }

    private var _binding: VB? = null
    private val binding: VB
        get() = _binding!!

    private lateinit var onBackPressedDispatcher: OnBackPressedDispatcher

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher = requireActivity().onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (onBackPressed()) {
                    remove()
                    performBackPress()
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = inflateViewBinding(inflater, container)
        val binding = binding
        if (binding is ViewDataBinding) {
            binding.lifecycleOwner = viewLifecycleOwner
            onSetupViewModel(binding)
        }
        return binding.root
    }

    protected open fun onSetupViewModel(binding: VB) = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onSetupActionBar(binding)
        onSetupView(binding)
    }

    protected open fun onSetupActionBar(binding: VB) = Unit

    protected open fun onSetupView(binding: VB) = Unit

    protected fun performBackPress() =
        onBackPressedDispatcher.onBackPressed()

    protected open fun onBackPressed() = true

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}