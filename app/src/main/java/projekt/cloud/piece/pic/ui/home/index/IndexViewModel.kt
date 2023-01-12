package projekt.cloud.piece.pic.ui.home.index

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import projekt.cloud.piece.pic.api.base.BaseApiRequest.Companion.request
import projekt.cloud.piece.pic.api.collections.Collections
import projekt.cloud.piece.pic.api.collections.CollectionsResponseBody
import projekt.cloud.piece.pic.api.comics.random.Random
import projekt.cloud.piece.pic.api.comics.random.RandomResponseBody
import projekt.cloud.piece.pic.base.BaseCallbackViewModel
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION

class IndexViewModel: BaseCallbackViewModel() {
    
    companion object IndexViewModelConstants {
        const val INDEX_COMPLETE = 0
        const val INDEX_IO_EXCEPTION = -1
        const val INDEX_EXCEPTION = -2
        const val INDEX_INVALID_STATE_CODE = -3
        const val INDEX_ERROR = -4
        const val INDEX_EMPTY_CONTENT = -5
        const val INDEX_REJECTED = -6
    }
    
    private val _collectionTitleA = MutableLiveData<String?>(null)
    val collectionTitleA: LiveData<String?>
        get() = _collectionTitleA
    
    val comicListA = arrayListOf<CollectionsResponseBody.Comic>()
    val comicListB = arrayListOf<CollectionsResponseBody.Comic>()
    val comicListRandom = arrayListOf<RandomResponseBody.Comic>()
    
    private val _collectionTitleB = MutableLiveData<String?>(null)
    val collectionTitleB: LiveData<String?>
        get() = _collectionTitleB
    
    var isCollectionsObtainComplete = false
        private set
    
    fun scopedObtainComics(scope: CoroutineScope, token: String): Job {
        return scope.ui {
            val collections = Collections(token).request()
            if (!collections.isComplete) {
                return@ui when (collections.state) {
                    STATE_IO_EXCEPTION -> {
                        setCallback(INDEX_IO_EXCEPTION, collections.message)
                    }
                    STATE_EXCEPTION -> {
                        setCallback(INDEX_EXCEPTION, collections.message)
                    }
                    else -> {
                        setCallback(INDEX_INVALID_STATE_CODE, collections.message)
                    }
                }
            }
            
            if (collections.isErrorResponse) {
                return@ui collections.errorResponse().let { errorResponseBody ->
                    setCallback(
                        INDEX_ERROR,
                        errorResponseBody.message,
                        errorResponseBody.code,
                        errorResponseBody.error,
                        errorResponseBody.detail
                    )
                }
            }
            
            if (collections.isEmptyResponse) {
                return@ui setCallback(INDEX_EMPTY_CONTENT)
            }
            
            if (collections.isRejected()) {
                return@ui setCallback(INDEX_REJECTED)
            }
            
            val collectionResponseBody = collections.responseBody()
            var collection = collectionResponseBody[0]
            _collectionTitleA.value = collection.title
            comicListA += collection.comicList
            
            collection = collectionResponseBody[1]
            _collectionTitleB.value = collection.title
            comicListB += collection.comicList
    
            val random = Random(token).request()
            if (!random.isComplete) {
                return@ui when (random.state) {
                    STATE_IO_EXCEPTION -> {
                        setCallback(INDEX_IO_EXCEPTION, random.message)
                    }
                    STATE_EXCEPTION -> {
                        setCallback(INDEX_EXCEPTION, random.message)
                    }
                    else -> {
                        setCallback(INDEX_INVALID_STATE_CODE, random.message)
                    }
                }
            }
    
            if (random.isErrorResponse) {
                return@ui random.errorResponse().let { errorResponseBody ->
                    setCallback(
                        INDEX_ERROR,
                        errorResponseBody.message,
                        errorResponseBody.code,
                        errorResponseBody.error,
                        errorResponseBody.detail
                    )
                }
            }
    
            if (random.isEmptyResponse) {
                return@ui setCallback(INDEX_EMPTY_CONTENT)
            }
    
            if (random.isRejected()) {
                return@ui setCallback(INDEX_REJECTED)
            }
            
            comicListRandom += random.responseBody().comicList
            
            isCollectionsObtainComplete = true
            setCallback(INDEX_COMPLETE)
        }
    }

}