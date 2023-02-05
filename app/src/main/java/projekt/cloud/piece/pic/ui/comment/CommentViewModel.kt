package projekt.cloud.piece.pic.ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import projekt.cloud.piece.pic.api.base.BaseApiRequest.BaseApiRequestUtil.request
import projekt.cloud.piece.pic.api.comics.comments.CommentsResponseBody
import projekt.cloud.piece.pic.api.comments.children.Children
import projekt.cloud.piece.pic.api.comments.children.ChildrenResponseBody
import projekt.cloud.piece.pic.api.comments.like.Like
import projekt.cloud.piece.pic.base.BaseCallbackViewModel
import projekt.cloud.piece.pic.util.CoroutineUtil.default
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION
import projekt.cloud.piece.pic.util.SerializeUtil.decodeJson

class CommentViewModel: BaseCallbackViewModel() {
    
    companion object CommentViewModelUtil {
        const val COMMENT_UPDATE = 1
        const val COMMENT_COMPLETE = 0
        const val COMMENT_IO_EXCEPTION = -1
        const val COMMENT_EXCEPTION = -2
        const val COMMENT_INVALID_STATE_CODE = -3
        const val COMMENT_ERROR = -4
        const val COMMENT_EMPTY_CONTENT = -5
        const val COMMENT_REJECTED = -6
    
        const val LIKE_COMPLETE = 10
        const val LIKE_IO_EXCEPTION = -11
        const val LIKE_EXCEPTION = -12
        const val LIKE_INVALID_STATE_CODE = -13
        const val LIKE_ERROR = -14
        const val LIKE_EMPTY_CONTENT = -15
        const val LIKE_REJECTED = -16
        
        private const val PAGE_DEFAULT_VALUE = 0
    }

    private var _id: String? = null
    private val id: String
        get() = _id!!
    
    private val _comment = MutableLiveData<CommentsResponseBody.Comment?>()
    val comment: LiveData<CommentsResponseBody.Comment?>
        get() = _comment
    
    private val _commentList = ArrayList<ChildrenResponseBody.Comment>()
    val commentList: List<ChildrenResponseBody.Comment>
        get() = _commentList
    
    fun scopedReflectComment(commentStr: String, coroutineScope: CoroutineScope = viewModelScope) {
        coroutineScope.ui {
            _comment.value = withContext(default) {
                commentStr.decodeJson<CommentsResponseBody.Comment>()
            }
        }
    }
    
    fun scopedObtainCommentReplies(token: String, id: String, coroutineScope: CoroutineScope = viewModelScope) {
        if (_id != id) {
            _id = id
            coroutineScope.ui {
                runBlocking(default) {
                    _commentList.clear()
                }
                obtainCommentReplies(token, id)
            }
        }
    }
    
    private suspend fun obtainCommentReplies(token: String, id: String) {
        var page = PAGE_DEFAULT_VALUE
        var pages: Int
        var children: Children
        var childrenResponseBody: ChildrenResponseBody
        do {
            children = Children(token, id, ++page).request()
            if (!children.isComplete) {
                return when (children.state) {
                    STATE_IO_EXCEPTION -> {
                        setCallback(COMMENT_IO_EXCEPTION, children.message)
                    }
                    STATE_EXCEPTION -> {
                        setCallback(COMMENT_EXCEPTION, children.message)
                    }
                    else -> {
                        setCallback(COMMENT_INVALID_STATE_CODE, children.message)
                    }
                }
            }
            
            if (children.isErrorResponse) {
                return children.errorResponse().let { errorResponseBody ->
                    setCallback(
                        COMMENT_ERROR,
                        errorResponseBody.message,
                        errorResponseBody.code,
                        errorResponseBody.error,
                        errorResponseBody.detail
                    )
                }
            }
            
            if (children.isEmptyResponse) {
                return setCallback(COMMENT_EMPTY_CONTENT)
            }
            
            if (children.isRejected()) {
                return setCallback(COMMENT_REJECTED)
            }
            
            childrenResponseBody = children.responseBody()
            runBlocking(default) {
                page = childrenResponseBody.page
                pages = childrenResponseBody.pages
                _commentList += childrenResponseBody.commentList
            }
            
            setCallback(COMMENT_UPDATE)
        } while (pages != page)
        
        setCallback(COMMENT_COMPLETE)
    }
    
    fun scopedPostLikeComic(token: String, id: String, coroutineScope: CoroutineScope) {
        coroutineScope.ui {
            postLikeComic(token, id)
        }
    }
    
    private suspend fun postLikeComic(token: String, id: String) {
        val like = Like(token, id).request()
        if (!like.isComplete) {
            return when (like.state) {
                STATE_IO_EXCEPTION -> {
                    setCallback(LIKE_IO_EXCEPTION, like.message)
                }
                STATE_EXCEPTION -> {
                    setCallback(LIKE_EXCEPTION, like.message)
                }
                else -> {
                    setCallback(LIKE_INVALID_STATE_CODE, like.message)
                }
            }
        }
    
        if (like.isErrorResponse) {
            return like.errorResponse().let { errorResponseBody ->
                setCallback(
                    LIKE_ERROR,
                    errorResponseBody.message,
                    errorResponseBody.code,
                    errorResponseBody.error,
                    errorResponseBody.detail
                )
            }
        }
    
        if (like.isEmptyResponse) {
            return setCallback(LIKE_EMPTY_CONTENT)
        }
    
        if (like.isRejected()) {
            return setCallback(LIKE_REJECTED)
        }
        
        setCallback(LIKE_COMPLETE, id, responseDetail = like.response)
    }

}