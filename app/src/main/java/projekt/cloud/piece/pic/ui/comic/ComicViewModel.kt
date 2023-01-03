package projekt.cloud.piece.pic.ui.comic

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import projekt.cloud.piece.pic.api.base.BaseApiRequest.Companion.request
import projekt.cloud.piece.pic.api.comics.metadata.ComicMetadata
import projekt.cloud.piece.pic.api.comics.metadata.ComicMetadataResponseBody.Comic
import projekt.cloud.piece.pic.api.comics.metadata.ComicMetadataResponseBody.Creator
import projekt.cloud.piece.pic.base.BaseCallbackViewModel
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION

class ComicViewModel: BaseCallbackViewModel() {
    
    companion object MetadataCallbackCode {
        const val METADATA_COMPLETE = 0
        const val METADATA_IO_EXCEPTION = -1
        const val METADATA_EXCEPTION = -2
        const val METADATA_INVALID_STATE_CODE = -3
        const val METADATA_ERROR = -4
        const val METADATA_EMPTY_CONTENT = -5
        const val METADATA_REJECTED = -6
    }

    private val _cover = MutableLiveData<Bitmap?>()
    val cover: LiveData<Bitmap?>
        get() = _cover
    fun setCover(bitmap: Bitmap?) {
        _cover.value = bitmap
    }
    
    private val _id = MutableLiveData<String?>()
    val id: LiveData<String?>
        get() = _id
    fun setId(id: String?) {
        _id.value = id
    }
    
    private val _title = MutableLiveData<String?>()
    val title: LiveData<String?>
        get() = _title
    fun setTitle(title: String?) {
        _title.value = title
    }
    
    private val _comic = MutableLiveData<Comic?>()
    val comic: LiveData<Comic?>
        get() = _comic
    
    private val _creator = MutableLiveData<Creator?>()
    val creator: LiveData<Creator?>
        get() = _creator
    
    private val _creatorAvatar = MutableLiveData<Bitmap?>()
    val creatorAvatar: MutableLiveData<Bitmap?>
        get() = _creatorAvatar
    
    fun scopedRequestComicMetadata(scope: CoroutineScope = viewModelScope, token: String, id: String) {
        if (_comic.value?.id != id) {
            _comic.value = null
            _creator.value = null
            _creatorAvatar.value = null
            
            scope.ui {
                val metadata = ComicMetadata(token, id).request()
                if (!metadata.isComplete) {
                    return@ui when (metadata.state) {
                        STATE_IO_EXCEPTION -> {
                            setCallback(METADATA_IO_EXCEPTION, metadata.message)
                        }
                        STATE_EXCEPTION -> {
                            setCallback(METADATA_EXCEPTION, metadata.message)
                        }
                        else -> {
                            setCallback(METADATA_INVALID_STATE_CODE, metadata.message)
                        }
                    }
                }
                if (metadata.isErrorResponse) {
                    return@ui metadata.errorResponse().let { errorResponseBody ->
                        setCallback(
                            METADATA_ERROR,
                            errorResponseBody.message,
                            errorResponseBody.code,
                            errorResponseBody.error,
                            errorResponseBody.detail
                        )
                    }
                }
                
                if (metadata.isEmptyResponse) {
                    return@ui setCallback(METADATA_EMPTY_CONTENT)
                }
                
                if (metadata.isRejected()) {
                    return@ui setCallback(METADATA_REJECTED)
                }
                
                val responseBody = metadata.responseBody()
                _comic.value = responseBody.comic
                _creator.value = responseBody.creator
                _creatorAvatar.value = responseBody.creator.avatar.obtainBitmap()
                
                setCallback(METADATA_COMPLETE)
            }
        }
    }

}