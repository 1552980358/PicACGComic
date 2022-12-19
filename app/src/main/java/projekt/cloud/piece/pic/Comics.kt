package projekt.cloud.piece.pic

import android.graphics.Bitmap
import android.util.Log
import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiComics.comics
import projekt.cloud.piece.pic.api.ApiComics.ComicsResponseBody
import projekt.cloud.piece.pic.api.ApiComics.ComicsResponseBody.Data.Comics.Doc
import projekt.cloud.piece.pic.api.CommonBody.ErrorResponseBody
import projekt.cloud.piece.pic.api.CommonParam.ComicsSort
import projekt.cloud.piece.pic.api.CommonParam.ComicsSort.NEW_TO_OLD
import projekt.cloud.piece.pic.base.BaseTaskViewModel
import projekt.cloud.piece.pic.util.CodeBook
import projekt.cloud.piece.pic.util.CodeBook.LIST_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.LIST_CODE_ERROR_REJECTED
import projekt.cloud.piece.pic.util.CodeBook.LIST_CODE_PART_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.LIST_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpUtil
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson

class Comics: BaseTaskViewModel() {
    
    companion object {
        private const val PAGE_DEFAULT_VALUE = 0
        private const val HAS_MORE_PAGE_DEFAULT_VALUE = true
        private const val KEY_DEFAULT_VALUE = ""
        private val SORT_DEFAULT_VALUE = NEW_TO_OLD
        
        private const val TAG = "Comics"
    }
    
    private var page = PAGE_DEFAULT_VALUE
    private var hasMorePage = HAS_MORE_PAGE_DEFAULT_VALUE
    var key = KEY_DEFAULT_VALUE
        private set
    
    val comicList = arrayListOf<Doc>()
    val coverImages = ObservableArrayMap<String, Bitmap?>()
    
    var sort = SORT_DEFAULT_VALUE
        private set
    
    private var job: Job? = null
    
    fun changeCategorySort(token: String, sort: ComicsSort) {
        Log.i(TAG, "changeSort category=$key sort=${sort.sortName}")
        this.sort = sort
        requestCategory(token, key)
    }
    
    fun requestCategory(token: String, category: String) {
        Log.i(TAG, "requestCategory category=$category sort=${sort.sortName}")
        val oldJob = job
        job = viewModelScope.ui {
            oldJob?.cancelAndJoin()
            clear()
            if (key != category) {
                key = category
            }
            requestCategoryPageInternal(token)
            job = null
        }
    }
    
    fun requestCategoryPage(token: String) {
        Log.i(TAG, "requestCategoryPage category=$key sort=${sort.sortName} page=${page.plus(1)}")
        val oldJob = job
        job = viewModelScope.ui {
            oldJob?.join()
            requestCategoryPageInternal(token)
            job = null
        }
    }
    
    private suspend fun requestCategoryPageInternal(token: String) {
        Log.i(TAG, "requestCategoryPageInternal category=$key sort=${sort.sortName} page=${page.plus(1)}")
        val httpResponse = withContext(io) {
            comics(token, ++page, key, sort.sortName)
        }
        val response = httpResponse.response
        if (httpResponse.code != CodeBook.HTTP_REQUEST_CODE_SUCCESS || response == null) {
            return setTaskReceipt(LIST_CODE_ERROR_CONNECTION, httpResponse.message)
        }
    
        if (response.code != HttpUtil.HTTP_RESPONSE_CODE_SUCCESS) {
            val errorResponse = withContext(io) {
                response.decodeJson<ErrorResponseBody>()
            }
            return setTaskReceipt(LIST_CODE_ERROR_REJECTED, errorResponse.message)
        }
        val comicsResponseBody = withContext(io) {
            response.decodeJson<ComicsResponseBody>()
        }
        val comics = comicsResponseBody.data.comics
        val docs = comics.docs
        comicList.addAll(docs)
        setTaskReceipt(LIST_CODE_PART_SUCCESS, null)
        docs.forEach {
            coverImages[it._id] = withContext(io) {
                it.thumb.bitmap
            }
        }
        
        if (comics.page == comics.pages) {
            hasMorePage = false
            setTaskReceipt(LIST_CODE_SUCCESS, null)
        }
    }
    
    override fun clear(lifecycleOwner: LifecycleOwner) {
        val oldJob = job
        job = viewModelScope.io {
            oldJob?.cancelAndJoin()
            clear()
            job = null
        }
        super.clear(lifecycleOwner)
    }
    
    fun clear() {
        key = KEY_DEFAULT_VALUE
        page = PAGE_DEFAULT_VALUE
        hasMorePage = HAS_MORE_PAGE_DEFAULT_VALUE
        comicList.clear()
        coverImages.clear()
    }
    
}