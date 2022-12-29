package projekt.cloud.piece.pic.base

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding

abstract class BaseCallbackFragment<VB: ViewBinding, VM: BaseCallbackViewModel>: BaseViewModelFragment<VB, VM>() {
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.registerCallback(viewLifecycleOwner) { code, message, responseCode, errorCode, responseDetail ->
            onCallbackReceived(code, message, responseCode, errorCode, responseDetail)
        }
        super.onViewCreated(view, savedInstanceState)
    }
    
    protected open fun onCallbackReceived(code: Int, message: String?, responseCode: Int?, errorCode: Int?, responseDetail: String?) = Unit
    
}