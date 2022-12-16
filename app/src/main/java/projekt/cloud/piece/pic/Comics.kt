package projekt.cloud.piece.pic

import android.graphics.Bitmap
import android.util.Log
import androidx.databinding.ObservableArrayMap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.ApiComics.comics
import projekt.cloud.piece.pic.api.ApiComics.ComicsResponseBody
import projekt.cloud.piece.pic.api.ApiComics.ComicsResponseBody.Data.Comics.Doc
import projekt.cloud.piece.pic.api.CommonBody.ErrorResponseBody
import projekt.cloud.piece.pic.api.CommonParam.SORT_NEW_TO_OLD
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
        private const val SORT_DEFAULT_VALUE = SORT_NEW_TO_OLD
    }
    
    private var page = PAGE_DEFAULT_VALUE
    private var hasMorePage = HAS_MORE_PAGE_DEFAULT_VALUE
    var key = KEY_DEFAULT_VALUE
        private set
    
    val comicList = arrayListOf<Doc>()
    val coverImages = ObservableArrayMap<String, Bitmap?>()
    
    var sort = SORT_DEFAULT_VALUE
        private set
    
    fun changeSort(token: String, sort: String) =
        requestCategory(token, key, sort)
    
    fun requestCategory(token: String, category: String, sort: String) {
        Log.i("Comics", "requestCategory category=$category sort=$sort")
        if (key != category) {
            key = category
        }
        if (this.sort != sort) {
            comicList.clear()
            coverImages.clear()
            page = PAGE_DEFAULT_VALUE
            hasMorePage = HAS_MORE_PAGE_DEFAULT_VALUE
            
            this.sort = sort
        }
        requestCategoryPage(token)
    }
    
    fun requestCategoryPage(token: String) {
        if (hasMorePage) {
            Log.i("Comics", "requestCategory category=$key sort=$sort page=${page.plus(1)}")
            viewModelScope.ui {
                val httpResponse = withContext(io) {
                    comics(token, ++page, key, sort)
                }
                val response = httpResponse.response
                if (httpResponse.code != CodeBook.HTTP_REQUEST_CODE_SUCCESS || response == null) {
                    return@ui setTaskReceipt(LIST_CODE_ERROR_CONNECTION, httpResponse.message)
                }
        
                if (response.code != HttpUtil.HTTP_RESPONSE_CODE_SUCCESS) {
                    val errorResponse = withContext(io) {
                        response.decodeJson<ErrorResponseBody>()
                    }
                    return@ui setTaskReceipt(LIST_CODE_ERROR_REJECTED, errorResponse.message)
                }
                val comicsResponseBody = withContext(io) {
                    response.decodeJson<ComicsResponseBody>()
                }
                val comics = comicsResponseBody.data.comics
                val docs = comics.docs
                io {
                    docs.forEach {
                        coverImages[it._id] = it.thumb.bitmap
                    }
                }
                comicList.addAll(docs)
        
                setTaskReceipt(LIST_CODE_PART_SUCCESS, null)
        
                if (comics.page == comics.pages) {
                    hasMorePage = false
                    setTaskReceipt(LIST_CODE_SUCCESS, null)
                }
            }
        }
    }
    
    override fun clear(lifecycleOwner: LifecycleOwner) {
        super.clear(lifecycleOwner)
        clear()
    }
    
    fun clear() {
        key = KEY_DEFAULT_VALUE
        page = PAGE_DEFAULT_VALUE
        hasMorePage = HAS_MORE_PAGE_DEFAULT_VALUE
        comicList.clear()
        coverImages.clear()
    }
    
}