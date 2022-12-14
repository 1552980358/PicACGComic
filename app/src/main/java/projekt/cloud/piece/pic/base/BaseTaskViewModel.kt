package projekt.cloud.piece.pic.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import projekt.cloud.piece.pic.util.TaskReceipt

abstract class BaseTaskViewModel: ViewModel() {
    
    private var _taskReceipt = MutableLiveData<TaskReceipt?>()
    val taskReceipt: LiveData<TaskReceipt?>
        get() = _taskReceipt
    fun setTaskReceipt(code: Int, message: String?) {
        _taskReceipt.value = TaskReceipt(taskReceiptIssuer, code, message)
    }
    protected abstract val taskReceiptIssuer: String
    open fun clear(lifecycleOwner: LifecycleOwner) {
        _taskReceipt.value = null
    }
    
}