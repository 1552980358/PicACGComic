package projekt.cloud.piece.pic.util

object CodeBook {
    
    const val HTTP_REQUEST_CODE_SUCCESS = 0
    const val HTTP_REQUEST_CODE_IO_EXCEPTION = 1
    const val HTTP_REQUEST_CODE_EXCEPTION = 2
    
    const val AUTH_CODE_SUCCESS = 0
    const val AUTH_CODE_ERROR_NO_ACCOUNT = 1
    const val AUTH_CODE_ERROR_ACCOUNT_INVALID = 2
    const val AUTH_CODE_ERROR_CONNECTION = 3
    
    const val CATEGORIES_CODE_SUCCESS = 0
    const val CATEGORIES_CODE_ERROR_CONNECTION = 1
    const val CATEGORIES_CODE_ERROR_REQUEST = 2
    
    const val ACCOUNT_DETAIL_CODE_SUCCESS = 0
    const val ACCOUNT_DETAIL_CODE_ERROR_CONNECTION = 1
    const val ACCOUNT_DETAIL_CODE_REJECTED = 2
    
    
}