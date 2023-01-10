package projekt.cloud.piece.pic.api.comics.favourites

import kotlinx.serialization.Serializable

@Serializable
data class FavouriteResponseBody(val code: Int, val message: String, private val data: Data) {
    
    private companion object {
        const val ACTION_FAVOURITE = "favourite"
    }
    
    val isFavourite: Boolean
        get() = data.action == ACTION_FAVOURITE
    
    @Serializable
    data class Data(val action: String)

}