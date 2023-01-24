package projekt.cloud.piece.pic.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import projekt.cloud.piece.pic.api.Sort
import projekt.cloud.piece.pic.api.Sort.NEW_TO_OLD
import projekt.cloud.piece.pic.api.base.BaseApiRequest.BaseApiRequestUtil.request
import projekt.cloud.piece.pic.api.comics.Comics
import projekt.cloud.piece.pic.api.comics.ComicsResponseBody.Comic
import projekt.cloud.piece.pic.base.BaseCallbackViewModel
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION

class CategoryViewModel: BaseCallbackViewModel() {

    companion object CategoryViewModelUtil {
        const val CATEGORY_UPDATED = 1
        const val CATEGORY_COMPLETE = 0
        const val CATEGORY_IO_EXCEPTION = -1
        const val CATEGORY_EXCEPTION = -2
        const val CATEGORY_INVALID_STATE_CODE = -3
        const val CATEGORY_ERROR = -4
        const val CATEGORY_EMPTY_CONTENT = -5
        const val CATEGORY_REJECTED = -6
    }
    
    private val _category = MutableLiveData<String>()
    val category: LiveData<String>
        get() = _category
    
    @Volatile
    private var page = 1
    
    @Volatile
    var hasMorePage = true
        private set

    private var sort = NEW_TO_OLD
    
    private val _comicList = ArrayList<Comic>()
    val comicList: List<Comic>
        get() = _comicList
    
    fun updateSort(token: String, sort: Sort, coroutineScope: CoroutineScope = viewModelScope) {
        this.sort = sort
        resetAndObtainCategoryComic(token, category.value!!, sort, coroutineScope)
    }
    
    fun scopedObtainCategoryComics(token: String, category: String, coroutineScope: CoroutineScope = viewModelScope) {
        this._category.value = category
        resetAndObtainCategoryComic(token, category, sort, coroutineScope)
    }
    
    fun scopedObtainNewPage(token: String, coroutineScope: CoroutineScope = viewModelScope) {
        if (hasMorePage) {
            coroutineScope.ui {
                obtainNewPage(token, category.value!!, ++page, sort)
            }
        }
    }
    
    private fun resetAndObtainCategoryComic(token: String, category: String, sort: Sort, coroutineScope: CoroutineScope = viewModelScope) {
        page = 1
        _comicList.clear()
        hasMorePage = true
        coroutineScope.ui {
            obtainNewPage(token, category, page, sort)
        }
    }
    
    private suspend fun obtainNewPage(token: String, category: String, page: Int, sort: Sort) {
        val comics = Comics(token, category, page, sort).request()
        
        if (!comics.isComplete) {
            return when (comics.state) {
                STATE_IO_EXCEPTION -> {
                    setCallback(CATEGORY_IO_EXCEPTION, comics.message)
                }
                STATE_EXCEPTION -> {
                    setCallback(CATEGORY_EXCEPTION, comics.message)
                }
                else -> {
                    setCallback(CATEGORY_INVALID_STATE_CODE, comics.message)
                }
            }
        }
        
        if (comics.isErrorResponse) {
            return comics.errorResponse().let { errorResponseBody ->
                setCallback(
                    CATEGORY_ERROR,
                    errorResponseBody.message,
                    errorResponseBody.code,
                    errorResponseBody.error,
                    errorResponseBody.detail
                )
            }
        }
        
        if (comics.isEmptyResponse) {
            return setCallback(CATEGORY_EMPTY_CONTENT)
        }
        
        if (comics.isRejected()) {
            return setCallback(CATEGORY_REJECTED)
        }
        
        val comicsResponseBody = comics.responseBody()
        _comicList += comicsResponseBody.comicList
        
        setCallback(CATEGORY_UPDATED)
        
        if (page == comicsResponseBody.pages) {
            hasMorePage = false
            setCallback(CATEGORY_COMPLETE)
        }
    }
    
}