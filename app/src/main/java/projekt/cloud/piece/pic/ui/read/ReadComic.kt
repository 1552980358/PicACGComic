package projekt.cloud.piece.pic.ui.read

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReadComic: ViewModel() {

    private val _prev = MutableLiveData<Int>()
    val prev: LiveData<Int>
        get() = _prev
    
    private val _next = MutableLiveData<Int>()
    val next: LiveData<Int>
        get() = _next
    
}