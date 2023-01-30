package projekt.cloud.piece.pic.ui.comic.comments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import java.util.ArrayList
import kotlinx.coroutines.CoroutineScope
import projekt.cloud.piece.pic.api.base.BaseApiRequest.BaseApiRequestUtil.request
import projekt.cloud.piece.pic.api.comics.comments.Comments
import projekt.cloud.piece.pic.api.comics.comments.CommentsResponseBody.Comment
import projekt.cloud.piece.pic.base.BaseCallbackViewModel
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION

class CommentsViewModel: BaseCallbackViewModel() {
    
    companion object CommentsViewModelUtil {
        const val COMMENTS_UPDATE = 1
        const val COMMENTS_COMPLETE = 0
        const val COMMENTS_IO_EXCEPTION = -1
        const val COMMENTS_EXCEPTION = -2
        const val COMMENTS_INVALID_STATE_CODE = -3
        const val COMMENTS_ERROR = -4
        const val COMMENTS_EMPTY_CONTENT = -5
        const val COMMENTS_REJECTED = -6
        
        private const val PAGE_DEFAULT_VALUE = 0
        private const val TOP_COMMENT_UPDATE_PAGE = 1
    }

    private val _topCommentList = ArrayList<Comment>()
    val topCommentList: List<Comment>
        get() = _topCommentList
    
    private val _commentList = ArrayList<Comment>()
    val commentList: List<Comment>
        get() = _commentList
    
    private var page = PAGE_DEFAULT_VALUE
    var hasMorePage = true
        private set
    
    private val _isUpdating = MutableLiveData(false)
    val isUpdating: LiveData<Boolean>
        get() = _isUpdating
    
    private var _id = MutableLiveData<String>()
    private val id: String
        get() = _id.value!!
    
    fun scopedObtainComments(token: String, id: String, coroutineScope: CoroutineScope = viewModelScope) {
        _isUpdating.value = true
        if (_id.value != id) {
            _id.value = id
            coroutineScope.ui {
                if (_topCommentList.isNotEmpty()) {
                    _topCommentList.clear()
                }
                if (_commentList.isNotEmpty()) {
                    _commentList.clear()
                }
                if (page != PAGE_DEFAULT_VALUE) {
                    page = PAGE_DEFAULT_VALUE
                }
                if (!hasMorePage) {
                    hasMorePage = true
                }
                obtainCommentsNewPage(token, id)
                _isUpdating.value = false
            }
        }
    }
    
    @Synchronized
    fun scopedObtainCommentsNewPage(token: String, coroutineScope: CoroutineScope = viewModelScope) {
        if (isUpdating.value == true) {
            return
        }
        _isUpdating.value = true
        if (hasMorePage) {
            coroutineScope.ui {
                obtainCommentsNewPage(token, id)
                _isUpdating.value = false
            }
        }
    }
    
    private suspend fun obtainCommentsNewPage(token: String, id: String) {
        obtainPagedComments(token, id, ++page)
    }
    
    private suspend fun obtainPagedComments(token: String, id: String, page: Int) {
        val comments = Comments(token, id, page).request()
        if (!comments.isComplete) {
            return when (comments.state) {
                STATE_IO_EXCEPTION -> {
                    setCallback(COMMENTS_IO_EXCEPTION, comments.message)
                }
                STATE_EXCEPTION -> {
                    setCallback(COMMENTS_EXCEPTION, comments.message)
                }
                else -> {
                    setCallback(COMMENTS_INVALID_STATE_CODE, comments.message)
                }
            }
        }
            
        if (comments.isErrorResponse) {
            return comments.errorResponse().let { errorResponseBody ->
                setCallback(
                    COMMENTS_ERROR,
                    errorResponseBody.message,
                    errorResponseBody.code,
                    errorResponseBody.error,
                    errorResponseBody.detail
                )
            }
        }
        
        if (comments.isEmptyResponse) {
            return setCallback(COMMENTS_EMPTY_CONTENT)
        }
        
        if (comments.isRejected()) {
            return setCallback(COMMENTS_REJECTED)
        }
        
        val commentsResponseBody = comments.responseBody()
        this.page = commentsResponseBody.page
        _commentList += commentsResponseBody.commentList
        if (page == TOP_COMMENT_UPDATE_PAGE) {
            _topCommentList += commentsResponseBody.topCommentList
        }
        
        setCallback(COMMENTS_UPDATE)
        
        if (page == commentsResponseBody.pages) {
            hasMorePage = false
            setCallback(COMMENTS_COMPLETE)
        }
    }

}