package projekt.cloud.piece.pic.api

import java.net.URLEncoder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.CommonBody.Image
import projekt.cloud.piece.pic.api.PicApi.API_COMICS
import projekt.cloud.piece.pic.api.PicApi.API_COMICS_INFO
import projekt.cloud.piece.pic.api.PicApi.API_URL
import projekt.cloud.piece.pic.api.PicApi.advancedSearchMethod
import projekt.cloud.piece.pic.api.PicApi.comicEpisodeContentOf
import projekt.cloud.piece.pic.api.PicApi.comicEpisodeOf
import projekt.cloud.piece.pic.api.RequestHeaders.generateHeaders
import projekt.cloud.piece.pic.util.HttpUtil.GET
import projekt.cloud.piece.pic.util.HttpUtil.POST
import projekt.cloud.piece.pic.util.HttpUtil.asParams
import projekt.cloud.piece.pic.util.HttpUtil.httpGet
import projekt.cloud.piece.pic.util.HttpUtil.httpPost
import projekt.cloud.piece.pic.util.SerializableUtil.encodeJson

object ApiComics {

    private const val PARAM_PAGE = "page"
    private const val PARAM_CATEGORY = "c"
    private const val PARAM_SORT = "s"

    @Serializable
    data class ComicsResponseBody(val code: Int,
                                  val message: String,
                                  val data: Data) {

        @Serializable
        data class Data(val comics: Comics) {

            @Serializable
            data class Comics(val docs: List<Doc>,
                              val total: Int,
                              val limit: Int,
                              val page: Int,
                              val pages: Int) {

                @Serializable
                data class Doc(
                    val _id: String,
                    val title: String,
                    val author: String,
                    val totalViews: Int = -1,
                    val totalLikes: Int = -1,
                    val pagesCount: Int,
                    val epsCount: Int,
                    val finished: Boolean,
                    val categories: List<String>,
                    val thumb: Image,
                    val id: String = "",
                    val likesCount: Int
                )

            }

        }

    }

    private fun comicsParams(page: Int, category: String, sort: String) =
        mapOf(PARAM_PAGE to page.toString(), PARAM_CATEGORY to URLEncoder.encode(category, Charsets.UTF_8.name()), PARAM_SORT to sort).asParams

    fun comics(token: String, page: Int, category: String, sort: String) =
        (API_COMICS + comicsParams(page, category, sort)).let {
            httpGet(API_URL + it, generateHeaders(it, GET, token))
        }
    
    @Serializable
    data class AdvancedSearchRequestBody(val categories: String, val keyword: String, val sort: String)
    
    @Serializable
    data class AdvancedSearchResponseBody(val code: Int, val message: String, val data: Data) {
        
        @Serializable
        data class Data(val comics: Comics) {
            
            @Serializable
            data class Comics(val docs: List<Doc>, val total: Int, val limit: Int, val page: Int, val pages: Int) {
                
                @Serializable
                data class Doc(
                    @SerialName("_id")
                    val id: String,
                    val title: String,
                    val author: String = "",
                    val description: String = "",
                    val chineseTeam: String = "",
                    val categories: List<String>,
                    val tags: List<String>,
                    @SerialName("created_at")
                    val createDate: String,
                    @SerialName("updated_at")
                    val updateDate: String,
                    val thumb: Image,
                    val finished: Boolean,
                    val likesCount: Int,
                    val totalViews: Int = -1,
                    val totalLikes: Int = -1,
                )
                
            }
            
        }
        
    }
    
    fun advancedSearch(categories: String, keyword: String, sort: String, page: Int, token: String) =
        advancedSearchMethod(page).let { method ->
            httpPost(
                API_URL + method,
                generateHeaders(method, POST, token),
                AdvancedSearchRequestBody(categories, keyword, sort).encodeJson
            )
        }

    @Serializable
    data class ComicResponseBody(val code: Int, val message: String, val data: Data) {

        @Serializable
        data class Data(val comic: Comic) {

            @Serializable
            data class Comic(@SerialName("_id")
                             val id: String,
                             @SerialName("_creator")
                             val creator: Creator,
                             val title: String,
                             val description: String,
                             val thumb: Image,
                             val author: String,
                             val chineseTeam: String = "",
                             val categories: List<String>,
                             val tags: List<String>,
                             val pagesCount: Int,
                             val epsCount: Int,
                             val finished: Boolean,
                             @SerialName("updated_at")
                             val updateDate: String,
                             @SerialName("created_at")
                             val createDate: String,
                             val allowDownload: Boolean,
                             val allowComment: Boolean,
                             val totalLikes: Int,
                             val totalViews: Int,
                             val totalComments: Int,
                             val viewsCount: Int,
                             val likesCount: Int,
                             val commentsCount: Int,
                             val isFavourite: Boolean,
                             val isLiked: Boolean) {

                @Serializable
                data class Creator(
                    @SerialName("_id")
                    val id: String,
                    val gender: String,
                    val name: String,
                    val slogan: String = "",
                    val title: String = "",
                    val verified: Boolean = false,
                    val exp: Int,
                    val level: Int,
                    val characters: List<String>,
                    val role: String,
                    val avatar: Image
                )

            }
        }
    }

    fun comic(id: String, token: String) =
        (API_COMICS_INFO + id).let { method ->
            httpGet(API_URL + method, generateHeaders(method, GET, token))
        }
    
    @Serializable
    data class EpisodeResponseBody(val code: Int, val message: String, val data: Data) {
        
        @Serializable
        data class Data(val eps: Episode) {
            
            @Serializable
            data class Episode(val docs: List<Doc>,
                               val total: Int,
                               val limit: Int,
                               val page: Int,
                               val pages: Int) {
                
                @Serializable
                data class Doc(
                    val _id: String,
                    val title: String,
                    val order: Int,
                    @SerialName("updated_at")
                    val updateDate: String,
                    val id: String
                )
                
            }
        }
    }
    
    fun episode(id: String, page: Int, token: String) =
        comicEpisodeOf(id, page).let { method ->
            httpGet(API_URL + method, generateHeaders(method, GET, token))
        }
    
    @Serializable
    data class EpisodeContentResponseBody(val code: Int, val message: String, val data: Data) {
        
        @Serializable
        data class Data(val pages: Pages, val ep: Ep) {
            
            @Serializable
            data class Pages(val docs: List<Doc>, val total: Int, val limit: Int, val page: Int, val pages: Int) {
                
                @Serializable
                data class Doc(val _id: String, val media: Image, val id: String)
                
            }
            
            @Serializable
            data class Ep(@SerialName("_id") val id: String, val title: String)
            
        }
        
    }
    
    private const val EPISODE_CONTENT_FIRST_PAGE = 1
    fun episodeContent(comicId: String, episodeOrder: Int, page: Int = EPISODE_CONTENT_FIRST_PAGE, token: String) =
        comicEpisodeContentOf(comicId, episodeOrder, page).let { method ->
            httpGet(API_URL + method, generateHeaders(method, GET, token))
        }

}