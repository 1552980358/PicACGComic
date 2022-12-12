package projekt.cloud.piece.pic

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.withContext
import okhttp3.Response
import projekt.cloud.piece.pic.api.ApiComics.ComicResponseBody
import projekt.cloud.piece.pic.api.ApiComics.comic
import projekt.cloud.piece.pic.api.ApiComics.ComicResponseBody.Data.Comic
import projekt.cloud.piece.pic.api.ApiComics.EpisodeResponseBody
import projekt.cloud.piece.pic.api.ApiComics.EpisodeResponseBody.Data.Episode.Doc
import projekt.cloud.piece.pic.api.ApiComics.episode
import projekt.cloud.piece.pic.api.CommonBody.ErrorResponseBody
import projekt.cloud.piece.pic.util.CodeBook.COMIC_DETAIL_CODE_ERROR_CONNECTION
import projekt.cloud.piece.pic.util.CodeBook.COMIC_DETAIL_CODE_ERROR_REJECTED
import projekt.cloud.piece.pic.util.CodeBook.COMIC_DETAIL_CODE_PART_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.COMIC_DETAIL_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CodeBook.HTTP_REQUEST_CODE_SUCCESS
import projekt.cloud.piece.pic.util.CompleteCallback
import projekt.cloud.piece.pic.util.CoroutineUtil.io
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpUtil.HTTP_RESPONSE_CODE_SUCCESS
import projekt.cloud.piece.pic.util.HttpUtil.HttpResponse
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson

class ComicDetail: ViewModel() {
    
    private val _comic = MutableLiveData<Comic?>()
    val comic: LiveData<Comic?>
        get() = _comic
    
    private val _cover = MutableLiveData<Bitmap?>()
    val cover: LiveData<Bitmap?>
        get() = _cover
    fun setCover(bitmap: Bitmap?) {
        _cover.value = bitmap
    }
    
    private val _avatar = MutableLiveData<Bitmap?>()
    val avatar: LiveData<Bitmap?>
        get() = _avatar
    
    var id: String? = null
    
    val docList = arrayListOf<Doc>()
    
    fun requestComicInfo(token: String, id: String, resources: Resources, completeCallback: CompleteCallback) {
        viewModelScope.ui {
            if (requestComic(token, id, resources, completeCallback)) {
                var httpResponse: HttpResponse
                var response: Response?
                var page = 0
                while (true) {
                    httpResponse = withContext(io) {
                        episode(id, ++page, token)
                    }
        
                    response = httpResponse.response
                    if (httpResponse.code != HTTP_REQUEST_CODE_SUCCESS || response == null) {
                        return@ui completeCallback.invoke(COMIC_DETAIL_CODE_ERROR_CONNECTION, httpResponse.message)
                    }
        
                    if (response.code != HTTP_RESPONSE_CODE_SUCCESS) {
                        val errorResponseBody = withContext(io) {
                            response.decodeJson<ErrorResponseBody>()
                        }
                        return@ui completeCallback.invoke(COMIC_DETAIL_CODE_ERROR_REJECTED, errorResponseBody.message)
                    }
                    
                    val episode = withContext(io) {
                        response.decodeJson<EpisodeResponseBody>()
                    }
                    
                    docList.addAll(episode.data.eps.docs)
                    
                    completeCallback.invoke(COMIC_DETAIL_CODE_PART_SUCCESS, null)
                    
                    if (episode.data.eps.page == episode.data.eps.pages) {
                        break
                    }
                }
                
                completeCallback.invoke(COMIC_DETAIL_CODE_SUCCESS, null)
            }
        }
    }
    
    private suspend fun requestComic(token: String, id: String, resources: Resources, completeCallback: CompleteCallback): Boolean {
        val httpResponse = withContext(io) {
            comic(id, token)
        }
    
        val response: Response? = httpResponse.response
        if (httpResponse.code != HTTP_REQUEST_CODE_SUCCESS || response == null) {
            completeCallback.invoke(COMIC_DETAIL_CODE_ERROR_CONNECTION, httpResponse.message)
            return false
        }
    
        if (response.code != HTTP_RESPONSE_CODE_SUCCESS) {
            val errorResponseBody = withContext(io) {
                response.decodeJson<ErrorResponseBody>()
            }
            completeCallback.invoke(COMIC_DETAIL_CODE_ERROR_REJECTED, errorResponseBody.message)
            return false
        }
    
        val comicResponse = withContext(io) {
            response.decodeJson<ComicResponseBody>()
        }
    
        val comic = comicResponse.data.comic
        _comic.value = comic
        _avatar.value = withContext(io) {
            comic.creator.avatar.bitmap ?: BitmapFactory.decodeResource(resources, R.drawable.ic_round_account_circle_24)
        }
        
        return true
    }
    
    fun clearAll(lifecycleOwner: LifecycleOwner) {
        id = null
        _comic.removeObservers(lifecycleOwner)
        _comic.removeObservers(lifecycleOwner)
        _avatar.removeObservers(lifecycleOwner)
        
        _comic.value = null
        _cover.value = null
        _avatar.value = null
        
        docList.clear()
    }
    
}