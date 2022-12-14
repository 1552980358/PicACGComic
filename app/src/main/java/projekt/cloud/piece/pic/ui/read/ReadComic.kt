package projekt.cloud.piece.pic.ui.read

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReadComic: ViewModel() {
    
    companion object {
        const val DEFAULT_ORDER = 0
    }
    
    var order = 0
        set(value) {
            val oldField = field
            field = value
            if (oldField != DEFAULT_ORDER && value != DEFAULT_ORDER) {
                when {
                    oldField > value -> _prev.value = value
                    oldField < value -> _next.value = value
                }
            }
        }
    
    private val _prev = MutableLiveData<Int>()
    val prev: LiveData<Int>
        get() = _prev
    
    private val _next = MutableLiveData<Int>()
    val next: LiveData<Int>
        get() = _next
    
}