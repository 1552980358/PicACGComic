package projekt.cloud.piece.pic.ui.viewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import projekt.cloud.piece.pic.api.base.BaseApiRequest.Companion.request
import projekt.cloud.piece.pic.api.comics.episode.Episode
import projekt.cloud.piece.pic.api.comics.episode.EpisodeResponseBody.EpisodeImage
import projekt.cloud.piece.pic.base.BaseCallbackViewModel
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_IO_EXCEPTION
import projekt.cloud.piece.pic.util.HttpRequest.HttpRequestUtil.HttpRequestState.STATE_EXCEPTION

class ViewerViewModel: BaseCallbackViewModel() {
    
    companion object ViewerViewModelCallbackCode {
        const val VIEWER_COMPLETE = 0
        const val VIEWER_IO_EXCEPTION = -1
        const val VIEWER_EXCEPTION = -2
        const val VIEWER_INVALID_STATE_CODE = -3
        const val VIEWER_ERROR = -4
        const val VIEWER_EMPTY_CONTENT = -5
        const val VIEWER_REJECTED = -6
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
    
    private val _order = MutableLiveData<Int>()
    val order: LiveData<Int>
        get() = _order
    fun setOrder(order: Int) {
        _order.value = order
    }
    
    private val _maxOrder = MutableLiveData<Int>()
    val maxOrder: LiveData<Int>
        get() = _maxOrder
    fun setMaxOrder(maxOrder: Int) {
        _maxOrder.value = maxOrder
    }
    
    val episodeImageList = arrayListOf<EpisodeImage>()
    
    fun scopedRequestComicImages(token: String, id: String, order: Int, scope: CoroutineScope) {
        scope.ui {
            episodeImageList.clear()
            
            var page = 0
            var pages: Int
            var episode: Episode
            
            do {
                episode = Episode(token, id, order, ++page).request()
    
                if (!episode.isComplete) {
                    return@ui when (episode.state) {
                        STATE_IO_EXCEPTION -> {
                            setCallback(VIEWER_IO_EXCEPTION, episode.message)
                        }
                        STATE_EXCEPTION -> {
                            setCallback(VIEWER_EXCEPTION, episode.message)
                        }
                        else -> {
                            setCallback(VIEWER_INVALID_STATE_CODE, episode.message)
                        }
                    }
                }
                if (episode.isErrorResponse) {
                    return@ui episode.errorResponse().let { errorResponseBody ->
                        setCallback(
                            VIEWER_ERROR,
                            errorResponseBody.message,
                            errorResponseBody.code,
                            errorResponseBody.error,
                            errorResponseBody.detail
                        )
                    }
                }
    
                if (episode.isEmptyResponse) {
                    setCallback(VIEWER_EMPTY_CONTENT)
                    return@ui
                }
    
                if (episode.isRejected()) {
                    setCallback(VIEWER_REJECTED)
                    return@ui
                }
                
                val episodeResponseBody = episode.responseBody()
                episodeImageList.addAll(episodeResponseBody.episodeImageList)
                pages = episodeResponseBody.pages
            } while (page != pages)
            
            episodeImageList.sortBy { it.media.originalName }
            setCallback(VIEWER_COMPLETE)
        }
    }
    
}