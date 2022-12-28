package projekt.cloud.piece.pic.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseCallbackViewModel: ViewModel() {

    private class Callback(val code: Int, val message: String?)
    private val _callback = MutableLiveData<Callback>()
    
    fun registerCallback(lifecycleOwner: LifecycleOwner, callback: (Int, String?) -> Unit) {
        _callback.observe(lifecycleOwner) {
            callback.invoke(it.code, it.message)
        }
    }

}