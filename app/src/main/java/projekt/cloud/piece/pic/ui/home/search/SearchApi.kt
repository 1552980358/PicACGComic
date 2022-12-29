package projekt.cloud.piece.pic.ui.home.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_INT
import projekt.cloud.piece.pic.api.ApiConstants.IGNORE_STRING
import projekt.cloud.piece.pic.api.Sort
import projekt.cloud.piece.pic.api.image.Image
import projekt.cloud.piece.pic.ui.home.search.SearchApi.SearchResponseBody.Data.Comics

object SearchApi {

    private const val API_SEARCH_PATH = "comics/advanced-search"
    private const val API_SEARCH_QUERY_PAGE = "page"

    @Serializable
    data class SearchRequestBody(val categories: String, val keyword: String, val sort: Sort)

    @Serializable
    data class SearchResponseBody(val code: Int, val message: String, private val data: Data) {

        val comics: Comics
            get() = data.comics

        @Serializable
        data class Data(val comics: Comics) {

            @Serializable
            data class Comics(@SerialName("docs")
                              val metadataList: List<Metadata>,
                              val total: Int,
                              val limit: Int,
                              val page: Int,
                              val pages: Int) {

                @Serializable
                data class Metadata(
                    @SerialName("_id")
                    val id: String,
                    val title: String,
                    val author: String = IGNORE_STRING,
                    val description: String = IGNORE_STRING,
                    val chineseTeam: String = IGNORE_STRING,
                    @SerialName("categories")
                    val categoryList: List<String>,
                    @SerialName("tags")
                    val tagList: List<String>,
                    @SerialName("created_at")
                    val createDate: String,
                    @SerialName("updated_at")
                    val updateDate: String,
                    @SerialName("thumb")
                    val cover: Image,
                    val finished: Boolean,
                    val likesCount: Int,
                    val totalViews: Int = IGNORE_INT,
                    val totalLikes: Int = IGNORE_INT,
                )

            }

        }

    }

}