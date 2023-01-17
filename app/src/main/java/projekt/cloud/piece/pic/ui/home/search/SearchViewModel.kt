package projekt.cloud.piece.pic.ui.home.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import projekt.cloud.piece.pic.api.Sort
import projekt.cloud.piece.pic.api.Sort.NEW_TO_OLD
import projekt.cloud.piece.pic.api.base.BaseApiRequest.BaseApiRequestUtil.request
import projekt.cloud.piece.pic.api.comics.search.AdvancedSearch
import projekt.cloud.piece.pic.api.comics.search.AdvancedSearchResponseBody.Comic
import projekt.cloud.piece.pic.base.BaseCallbackViewModel
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION

class SearchViewModel: BaseCallbackViewModel() {
    
    companion object SearchViewModelUtil {
        const val SEARCH_PAGE_UPDATE = 1
        const val SEARCH_COMPLETE = 0
        const val SEARCH_IO_EXCEPTION = -1
        const val SEARCH_EXCEPTION = -2
        const val SEARCH_INVALID_STATE_CODE = -3
        const val SEARCH_ERROR = -4
        const val SEARCH_EMPTY_CONTENT = -5
        const val SEARCH_REJECTED = -6
    }

    var sort = NEW_TO_OLD
        private set
    
    private val _keyword = MutableLiveData<String>()
    val keyword: LiveData<String>
        get() = _keyword
    
    val comicList = arrayListOf<Comic>()
    
    private val _resultsFound = MutableLiveData(0)
    val resultsFound: LiveData<Int>
        get() = _resultsFound
    
    private val categoryList = arrayListOf<String>()
    
    @Volatile
    private var page = 0
    private var pages = 0
    
    fun updateSort(token: String, sort: Sort, coroutineScope: CoroutineScope) {
        if (this.sort != sort) {
            this.sort = sort
            scopedAdvancedSearch(token, categoryList, keyword.value!!, coroutineScope)
        }
    }
    
    fun scopedUpdatePage(token: String, coroutineScope: CoroutineScope) {
        coroutineScope.ui {
            requestAdvancedSearchInternal(token, categoryList, keyword.value!!, sort, ++page)
        }
    }
    
    fun scopedAdvancedSearch(token: String, categoryList: List<String>, keyword: String, coroutineScope: CoroutineScope) {
        _keyword.value = keyword
        with(this.categoryList) {
            clear()
            this += categoryList
        }
        comicList.clear()
        page = 0
        scopedUpdatePage(token, coroutineScope)
    }
    
    private suspend fun requestAdvancedSearchInternal(token: String, categoryList: List<String>, keyword: String, sort: Sort, page: Int) {
        val advancedSearch = AdvancedSearch(token, categoryList, keyword, sort, page).request()
        if (!advancedSearch.isComplete) {
            return when (advancedSearch.state) {
                STATE_IO_EXCEPTION -> {
                    setCallback(SEARCH_IO_EXCEPTION, advancedSearch.message)
                }
                STATE_EXCEPTION -> {
                    setCallback(SEARCH_EXCEPTION, advancedSearch.message)
                }
                else -> {
                    setCallback(SEARCH_INVALID_STATE_CODE, advancedSearch.message)
                }
            }
        }
        
        if (advancedSearch.isErrorResponse) {
            return advancedSearch.errorResponse().let { errorResponseBody ->
                setCallback(
                    SEARCH_ERROR,
                    errorResponseBody.message,
                    errorResponseBody.code,
                    errorResponseBody.error,
                    errorResponseBody.detail
                )
            }
        }
        
        if (advancedSearch.isEmptyResponse) {
            return setCallback(SEARCH_EMPTY_CONTENT)
        }
        
        if (advancedSearch.isRejected()) {
            return setCallback(SEARCH_REJECTED)
        }
        
        val advancedSearchResponseBody = advancedSearch.responseBody()
        if (resultsFound.value != advancedSearchResponseBody.total) {
            _resultsFound.value = advancedSearchResponseBody.total
        }
        comicList += advancedSearchResponseBody.comicList
        setCallback(SEARCH_PAGE_UPDATE)
        
        pages = advancedSearchResponseBody.pages
        if (pages == page) {
            setCallback(SEARCH_COMPLETE)
        }
    }
    
}