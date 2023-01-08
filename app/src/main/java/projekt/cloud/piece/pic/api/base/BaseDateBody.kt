package projekt.cloud.piece.pic.api.base

import kotlinx.serialization.Transient

typealias Date = String

abstract class BaseDateBody {
    
    private companion object {
        @Transient
        const val DATE_DIVIDER = 'T'
        @Transient
        const val DATE_END = "Z"
        @Transient
        const val DATE_TIME_DIVIDER = ' '
    }
    
    protected val Date.str: String
        get() = let { date ->
            date.indexOf(DATE_DIVIDER).let { divider ->
                date.substring(0, divider) + DATE_TIME_DIVIDER + date.substring(divider + 1, date.indexOf(DATE_END))
            }
        }
    
}