package projekt.cloud.piece.pic.util

interface AdapterInterface {
    
    fun notifyUpdate()
    
    fun notifyClear()
    
    fun notifyUpdate(index: Int) = Unit
    
    fun notifyUpdate(index: Int, value: Any?) = Unit
    
}