package projekt.cloud.piece.pic.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseViewModelFragment<VB: ViewBinding, VM: ViewModel>: BaseFragment<VB>() {
    
    protected lateinit var viewModel: VM
    
    @Suppress("UNCHECKED_CAST")
    private val viewModelClass: Class<VM>
        get() = ((this::class.java.genericSuperclass as ParameterizedType)
            .actualTypeArguments[1] as Class<VM>)
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(viewModelOwner)[viewModelClass]
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    
    protected open val viewModelOwner: ViewModelStoreOwner
        get() = this

}