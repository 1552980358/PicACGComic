package projekt.cloud.piece.pic.ui.home.index

import android.graphics.Bitmap
import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import projekt.cloud.piece.pic.api.base.BaseApiRequest.Companion.request
import projekt.cloud.piece.pic.api.collections.Collections
import projekt.cloud.piece.pic.api.collections.CollectionsResponseBody.Data.Collection.Comic
import projekt.cloud.piece.pic.base.BaseCallbackViewModel
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION

class IndexViewModel: BaseCallbackViewModel() {
    
    companion object IndexViewModelConstants {
        const val COLLECTIONS_COMPLETE = 0
        const val COLLECTIONS_IO_EXCEPTION = -1
        const val COLLECTIONS_EXCEPTION = -2
        const val COLLECTIONS_INVALID_STATE_CODE = -3
        const val COLLECTIONS_ERROR = -4
        const val COLLECTIONS_EMPTY_CONTENT = -5
        const val COLLECTIONS_REJECTED = -6
    }
    
    val coverMap = ObservableArrayMap<String, Bitmap?>()
    
    private val _collectionTitleA = MutableLiveData<String?>(null)
    val collectionTitleA: LiveData<String?>
        get() = _collectionTitleA
    
    val comicListA = arrayListOf<Comic>()
    val comicListB = arrayListOf<Comic>()
    
    private val _collectionTitleB = MutableLiveData<String?>(null)
    val collectionTitleB: LiveData<String?>
        get() = _collectionTitleB
    
    var isCollectionsObtainComplete = false
        private set
    
    fun scopedObtainCollections(scope: CoroutineScope, token: String): Job {
        return scope.ui {
            val collections = Collections(token).request()
            if (!collections.isComplete) {
                return@ui when (collections.state) {
                    STATE_IO_EXCEPTION -> {
                        setCallback(COLLECTIONS_IO_EXCEPTION, collections.message)
                    }
                    STATE_EXCEPTION -> {
                        setCallback(COLLECTIONS_EXCEPTION, collections.message)
                    }
                    else -> {
                        setCallback(COLLECTIONS_INVALID_STATE_CODE, collections.message)
                    }
                }
            }
            
            if (collections.isErrorResponse) {
                return@ui collections.errorResponse().let { errorResponseBody ->
                    setCallback(
                        COLLECTIONS_ERROR,
                        errorResponseBody.message,
                        errorResponseBody.code,
                        errorResponseBody.error,
                        errorResponseBody.detail
                    )
                }
            }
            
            if (collections.isEmptyResponse) {
                return@ui setCallback(COLLECTIONS_EMPTY_CONTENT)
            }
            
            if (collections.isRejected()) {
                return@ui setCallback(COLLECTIONS_REJECTED)
            }
            coverMap.clear()
            
            val collectionResponseBody = collections.responseBody()
            var collection = collectionResponseBody[0]
            _collectionTitleA.value = collection.title
            comicListA.addAll(collection.comicList)
            
            collection = collectionResponseBody[1]
            _collectionTitleB.value = collection.title
            comicListB.addAll(collection.comicList)
            
            comicListA.forEach { comic ->
                coverMap[comic.id] = comic.cover.obtainBitmap()
            }
            comicListB.forEach { comic ->
                coverMap[comic.id] = comic.cover.obtainBitmap()
            }
            isCollectionsObtainComplete = true
            setCallback(COLLECTIONS_COMPLETE)
        }
    }

}