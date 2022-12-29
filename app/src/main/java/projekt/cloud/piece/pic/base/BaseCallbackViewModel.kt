package projekt.cloud.piece.pic.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseCallbackViewModel: ViewModel() {

    private class Callback(
        val code: Int,
        val message: String?,
        val responseCode: Int?,
        val errorCode: Int?,
        val responseDetail: String?
    )
    
    private val _callback = MutableLiveData<Callback>()
    
    fun registerCallback(lifecycleOwner: LifecycleOwner, callback: (Int, String?, Int?, Int?, String?) -> Unit) {
        _callback.observe(lifecycleOwner) {
            callback.invoke(it.code, it.message, it.responseCode, it.errorCode, it.responseDetail)
        }
    }
    
    fun setCallback(code: Int,
                    message: String? = null,
                    responseCode: Int? = null,
                    errorCode: Int? = null,
                    responseDetail: String? = null) {
        _callback.value = Callback(code, message, responseCode, errorCode, responseDetail)
    }

}