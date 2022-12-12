package projekt.cloud.piece.pic.ui.read

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import projekt.cloud.piece.pic.api.ApiComics.EpisodeResponseBody.Data.Episode.Doc

class ReadComic: ViewModel() {

    var doc: Doc? = null
        set(value) {
            val oldField = field
            field = value
            if (oldField != null && value != null) {
                @Suppress("USELESS_CAST")
                when {
                    oldField.order > value.order -> _next.value = value as Doc
                    oldField.order < value.order -> _prev.value = value as Doc
                }
            }
        }
    
    private val _prev = MutableLiveData<Doc>()
    val prev: LiveData<Doc>
        get() = _prev
    
    private val _next = MutableLiveData<Doc>()
    val next: LiveData<Doc>
        get() = _next
    
}