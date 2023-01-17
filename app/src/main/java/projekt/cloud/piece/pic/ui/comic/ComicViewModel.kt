package projekt.cloud.piece.pic.ui.comic

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import projekt.cloud.piece.pic.api.base.BaseApiRequest.Companion.request
import projekt.cloud.piece.pic.api.comics.episodes.Episodes
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_COMPLETE
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_ERROR
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_REJECTED
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.METADATA_INVALID_STATE_CODE
import projekt.cloud.piece.pic.api.comics.episodes.EpisodesResponseBody.Episode
import projekt.cloud.piece.pic.api.comics.favourites.Favourite
import projekt.cloud.piece.pic.api.comics.like.Like
import projekt.cloud.piece.pic.api.comics.metadata.ComicMetadata
import projekt.cloud.piece.pic.api.comics.metadata.ComicMetadataResponseBody.Comic
import projekt.cloud.piece.pic.api.comics.metadata.ComicMetadataResponseBody.Creator
import projekt.cloud.piece.pic.api.image.Image
import projekt.cloud.piece.pic.base.BaseCallbackViewModel
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_COMPLETE
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_ERROR
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.EPISODES_REJECTED
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_COMPLETE
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_ERROR
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.FAVOURITE_REJECTED
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.LIKE_COMPLETE
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.LIKE_EMPTY_CONTENT
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.LIKE_ERROR
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.LIKE_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.LIKE_INVALID_STATE_CODE
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.LIKE_IO_EXCEPTION
import projekt.cloud.piece.pic.ui.comic.ComicViewModel.ComicViewModelCallbackCode.LIKE_REJECTED
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION

class ComicViewModel: BaseCallbackViewModel() {
    
    object ComicViewModelCallbackCode {
        const val METADATA_COMPLETE = 0
        const val METADATA_IO_EXCEPTION = -1
        const val METADATA_EXCEPTION = -2
        const val METADATA_INVALID_STATE_CODE = -3
        const val METADATA_ERROR = -4
        const val METADATA_EMPTY_CONTENT = -5
        const val METADATA_REJECTED = -6
        
        const val EPISODES_COMPLETE = 10
        const val EPISODES_IO_EXCEPTION = -11
        const val EPISODES_EXCEPTION = -12
        const val EPISODES_INVALID_STATE_CODE = -13
        const val EPISODES_ERROR = -14
        const val EPISODES_EMPTY_CONTENT = -15
        const val EPISODES_REJECTED = -16
        
        const val FAVOURITE_COMPLETE = 20
        const val FAVOURITE_IO_EXCEPTION = -21
        const val FAVOURITE_EXCEPTION = -22
        const val FAVOURITE_INVALID_STATE_CODE = -23
        const val FAVOURITE_ERROR = -24
        const val FAVOURITE_EMPTY_CONTENT = -25
        const val FAVOURITE_REJECTED = -26
    
        const val LIKE_COMPLETE = 30
        const val LIKE_IO_EXCEPTION = -31
        const val LIKE_EXCEPTION = -32
        const val LIKE_INVALID_STATE_CODE = -33
        const val LIKE_ERROR = -34
        const val LIKE_EMPTY_CONTENT = -35
        const val LIKE_REJECTED = -36
    }
    
    private companion object {
        const val PAGE_BEGIN = 0
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
    
    private val _creatorAvatar = MutableLiveData<Image?>(null)
    val creatorAvatar: LiveData<Image?>
        get() = _creatorAvatar
    
    private val _isFavourite = MutableLiveData<Boolean>()
    val isFavourite: LiveData<Boolean>
        get() = _isFavourite
    
    private val _isLiked = MutableLiveData<Boolean>()
    val isLiked: LiveData<Boolean>
        get() = _isLiked
    
    val episodeList = mutableListOf<Episode>()
    
    fun scopedRequestComicData(scope: CoroutineScope = viewModelScope, token: String, id: String) {
        if (_comic.value?.id != id) {
            _comic.value = null
            _creator.value = null
            _creatorAvatar.value = null
            
            scope.ui {
                // Requesting should not be launched at the same time,
                // otherwise, request will be rejected by server
                if (requestComicMetadata(token, id)) {
                    requestComicEpisodes(token, id)
                }
            }
        }
    }
    
    private suspend fun requestComicMetadata(token: String, id: String): Boolean {
        val metadata = ComicMetadata(token, id).request()
        
        if (!metadata.isComplete) {
            when (metadata.state) {
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
            return false
        }
        if (metadata.isErrorResponse) {
            metadata.errorResponse().let { errorResponseBody ->
                setCallback(
                    METADATA_ERROR,
                    errorResponseBody.message,
                    errorResponseBody.code,
                    errorResponseBody.error,
                    errorResponseBody.detail
                )
            }
            return false
        }
        
        if (metadata.isEmptyResponse) {
            setCallback(METADATA_EMPTY_CONTENT)
            return false
        }
        
        if (metadata.isRejected()) {
            setCallback(METADATA_REJECTED)
            return false
        }
        
        val responseBody = metadata.responseBody()
        _comic.value = responseBody.comic
        _creator.value = responseBody.creator
        _creatorAvatar.value = responseBody.creator.avatar
        _isFavourite.value = responseBody.comic.isFavourite
        _isLiked.value = responseBody.comic.isLiked
        
        setCallback(METADATA_COMPLETE)
        
        return true
    }
    
    private suspend fun requestComicEpisodes(token: String, id: String) {
        episodeList.clear()
        
        var page = PAGE_BEGIN
        var pages: Int
        var episodes: Episodes
        
        do {
            episodes = Episodes(token, id, ++page).request()
            if (!episodes.isComplete) {
                return when (episodes.state) {
                    STATE_IO_EXCEPTION -> {
                        setCallback(EPISODES_IO_EXCEPTION, episodes.message)
                    }
                    STATE_EXCEPTION -> {
                        setCallback(EPISODES_EXCEPTION, episodes.message)
                    }
                    else -> {
                        setCallback(EPISODES_INVALID_STATE_CODE, episodes.message)
                    }
                }
            }
            
            if (episodes.isErrorResponse) {
                return episodes.errorResponse().let { errorResponseBody ->
                    setCallback(
                        EPISODES_ERROR,
                        errorResponseBody.message,
                        errorResponseBody.code,
                        errorResponseBody.error,
                        errorResponseBody.detail
                    )
                }
            }
            
            if (episodes.isEmptyResponse) {
                return setCallback(EPISODES_EMPTY_CONTENT)
            }
            
            if (episodes.isRejected()) {
                return setCallback(EPISODES_REJECTED)
            }
            
            val episodesResponseBody = episodes.responseBody()
            pages = episodesResponseBody.episodes.pages
            episodeList += episodesResponseBody.episodes.episodeList
        } while (page != pages)
        
        setCallback(EPISODES_COMPLETE)
    }
    
    fun scopedUpdateFavourite(token: String, id: String, scope: CoroutineScope) {
        scope.ui {
            val favourite = Favourite(token, id).request()
            if (!favourite.isComplete) {
                return@ui when (favourite.state) {
                    STATE_IO_EXCEPTION -> {
                        setCallback(FAVOURITE_IO_EXCEPTION, favourite.message)
                    }
                    STATE_EXCEPTION -> {
                        setCallback(FAVOURITE_EXCEPTION, favourite.message)
                    }
                    else -> {
                        setCallback(FAVOURITE_INVALID_STATE_CODE, favourite.message)
                    }
                }
            }
            
            if (favourite.isErrorResponse) {
                return@ui favourite.errorResponse().let { errorResponseBody ->
                    setCallback(
                        FAVOURITE_ERROR,
                        errorResponseBody.message,
                        errorResponseBody.code,
                        errorResponseBody.error,
                        errorResponseBody.detail
                    )
                }
            }
            
            if (favourite.isEmptyResponse) {
                return@ui setCallback(FAVOURITE_EMPTY_CONTENT)
            }
            
            if (favourite.isRejected()) {
                return@ui setCallback(FAVOURITE_REJECTED)
            }
            
            val favouriteResponseBody = favourite.responseBody()
            _isFavourite.value = favouriteResponseBody.isFavourite
            setCallback(FAVOURITE_COMPLETE)
        }
    }
    
    fun scopedUpdateLiked(token: String, id: String, scope: CoroutineScope) {
        scope.ui {
            val like = Like(token, id).request()
    
            if (!like.isComplete) {
                return@ui when (like.state) {
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
                return@ui like.errorResponse().let { errorResponseBody ->
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
                return@ui setCallback(LIKE_EMPTY_CONTENT)
            }
    
            if (like.isRejected()) {
                return@ui setCallback(LIKE_REJECTED)
            }
            
            val likeResponseBody = like.responseBody()
            _isLiked.value = likeResponseBody.isLiked
            setCallback(LIKE_COMPLETE)
        }
    }
    
}