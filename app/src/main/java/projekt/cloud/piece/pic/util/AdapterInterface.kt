package projekt.cloud.piece.pic.util

interface AdapterInterface {
    
    fun notifyUpdate() = Unit
    
    fun notifyClear() = Unit
    
    fun notifyUpdate(index: Int) = Unit
    
    fun notifyUpdate(index: Int, value: Any?) = Unit
    
}