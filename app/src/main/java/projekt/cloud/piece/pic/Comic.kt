package projekt.cloud.piece.pic

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.withContext
import okhttp3.Response
import projekt.cloud.piece.pic.api.ApiComics
import projekt.cloud.piece.pic.util.CoroutineUtil
import projekt.cloud.piece.pic.util.CoroutineUtil.ui
import projekt.cloud.piece.pic.util.HttpUtil
import projekt.cloud.piece.pic.util.RequestFailedMethodBlock
import projekt.cloud.piece.pic.util.RequestSuccessMethodBlock
import projekt.cloud.piece.pic.util.ResponseUtil.decodeJson

class Comic: ViewModel() {
    
    companion object {
        private const val EPISODE_PAGE_INCREMENTING_DIFF = 1
    }
    
    private val _comic = MutableLiveData<ApiComics.ComicResponseBody.Data.Comic?>()
    val comic: LiveData<ApiComics.ComicResponseBody.Data.Comic?>
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
    
    private val episodeList = arrayListOf<ApiComics.EpisodeResponseBody.Data.Episode>()
    val docList = arrayListOf<ApiComics.EpisodeResponseBody.Data.Episode.Doc>()
    
    fun requestComicInfo(token: String?,
                         success: RequestSuccessMethodBlock,
                         failed: RequestFailedMethodBlock) {
        
        token ?: return failed.invoke(R.string.comic_detail_snack_not_logged)
        val id = id ?: return failed.invoke(R.string.comic_detail_snack_arg_required)
        
        viewModelScope.ui {
            // val comicResponse = withContext(CoroutineUtil.io) {
            //     ApiComics.comic(id, token)
            // } ?: return@ui failed.invoke(R.string.comic_detail_exception)
            //
            // if (comicResponse.code != HttpUtil.RESPONSE_CODE_SUCCESS) {
            //     return@ui failed.invoke(R.string.comic_detail_error_code)
            // }
            
            //val comic = comicResponse.decodeJson<ApiComics.ComicResponseBody>().data.comic
            //_comic.value = comic
            //
            //_avatar.value = withContext(CoroutineUtil.io) {
            //    comic.creator.avatar.bitmap
            //}
            
            //var episodeResponse: Response
            //var episode: ApiComics.EpisodeResponseBody.Data.Episode
            //var complete = false
            //while (!complete) {
            //    episodeResponse = withContext(CoroutineUtil.io) {
            //        ApiComics.episode(id, episodeList.size + EPISODE_PAGE_INCREMENTING_DIFF, token)
            //    } ?: return@ui failed.invoke(R.string.comic_detail_exception)
            //
            //    if (episodeResponse.code != HttpUtil.RESPONSE_CODE_SUCCESS) {
            //        return@ui failed.invoke(R.string.comic_detail_error_code)
            //    }
            //
            //    episode = episodeResponse.decodeJson<ApiComics.EpisodeResponseBody>().data.eps
            //    episodeList.add(episode)
            //    docList.addAll(episode.docs)
            //
            //    if (episodeList.size == episode.pages) {
            //        complete = true
            //    }
            //}
            //
            //success.invoke()
        }
    }
    
    fun clearAll() {
        id = null
        _comic.value = null
        _cover.value = null
        _avatar.value = null
        episodeList.clear()
        docList.clear()
    }
    
}