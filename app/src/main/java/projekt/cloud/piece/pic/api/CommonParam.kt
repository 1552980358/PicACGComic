package projekt.cloud.piece.pic.api

object CommonParam {
    
    private const val SORT_NEW_TO_OLD = "dd"
    private const val SORT_OLD_TO_NEW = "da"
    private const val SORT_MORE_HEART = "lv"
    private const val SORT_MORE_FAVOURITE = "vd"
    enum class ComicsSort(val sortName: String) {
        NEW_TO_OLD(SORT_NEW_TO_OLD),
        OLD_TO_NEW(SORT_OLD_TO_NEW),
        MORE_HEART(SORT_MORE_HEART),
        MORE_FAVOURITE(SORT_MORE_FAVOURITE);
    }

}