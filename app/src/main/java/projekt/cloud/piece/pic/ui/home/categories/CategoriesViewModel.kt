package projekt.cloud.piece.pic.ui.home.categories

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import projekt.cloud.piece.pic.api.base.BaseApiRequest.BaseApiRequestUtil.request
import projekt.cloud.piece.pic.api.comics.categories.Categories
import projekt.cloud.piece.pic.api.comics.categories.CategoriesResponseBody.Category
import projekt.cloud.piece.pic.base.BaseCallbackViewModel
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION

class CategoriesViewModel: BaseCallbackViewModel() {
    
    companion object CategoriesViewModelUtil {
        const val CATEGORIES_COMPLETE = 0
        const val CATEGORIES_IO_EXCEPTION = -1
        const val CATEGORIES_EXCEPTION = -2
        const val CATEGORIES_INVALID_STATE_CODE = -3
        const val CATEGORIES_ERROR = -4
        const val CATEGORIES_EMPTY_CONTENT = -5
        const val CATEGORIES_REJECTED = -6
    }

    private val _categoryList = arrayListOf<Category>()
    val categoryList: List<Category>
        get() = _categoryList
    
    var isCategoryObtainComplete = false
        private set
    
    fun scopedObtainCategories(token: String, coroutineScope: CoroutineScope = viewModelScope) {
        if (!isCategoryObtainComplete) {
            coroutineScope.ui {
                obtainCategories(token)
            }
        }
    }
    
    private suspend fun obtainCategories(token: String) {
        val categories = Categories(token).request()
        if (!categories.isComplete) {
            return setCallback(
                when (categories.state) {
                    STATE_IO_EXCEPTION -> CATEGORIES_IO_EXCEPTION
                    STATE_EXCEPTION ->  CATEGORIES_EXCEPTION
                    else -> CATEGORIES_INVALID_STATE_CODE
                },
                categories.message
            )
        }
        if (categories.isErrorResponse) {
            return categories.errorResponse().let { errorResponseBody ->
                setCallback(
                    CATEGORIES_ERROR,
                    errorResponseBody. message,
                    errorResponseBody. code,
                    errorResponseBody. error,
                    errorResponseBody. detail
                )
            }
        }
        if (categories.isEmptyResponse) {
            return setCallback(CATEGORIES_EMPTY_CONTENT)
        }
        if (categories.isRejected()) {
            return setCallback(CATEGORIES_REJECTED)
        }
        
        val categoriesResponseBody = categories.responseBody()
        _categoryList += categoriesResponseBody.categoryList.filter { it.active && !it.isWeb }
        
        isCategoryObtainComplete = true
        setCallback(CATEGORIES_COMPLETE)
    }

}