package projekt.cloud.piece.pic.api

object PicApi {

    const val API_URL = "https://picaapi.picacomic.com/"

    const val API_SIGN_IN = "auth/sign-in"

    const val API_USER_PROFILE = "users/profile"

    const val API_CATEGORIES = "categories"

    const val API_COMICS = "comics"
    const val API_COMICS_INFO = "comics/"
    fun comicEpisodeOf(id: String, page: Int) =
        "$API_COMICS/$id/eps?page=$page"
    
    fun comicEpisodeContentOf(id: String, order: Int, page: Int) =
        "$API_COMICS/$id/order/$order/pages?page=$page"
    
    fun advancedSearchMethod(page: Int) =
        "$API_COMICS/advanced-search?page=$page"

}