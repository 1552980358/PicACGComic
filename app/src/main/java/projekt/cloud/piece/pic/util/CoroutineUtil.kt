package projekt.cloud.piece.pic.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object CoroutineUtil {

    val io = Dispatchers.IO
    val ui = Dispatchers.Main
    val default = Dispatchers.Default

    fun io(block: suspend CoroutineScope.() -> Unit) =
        CoroutineScope(io).launch(block = block)

    fun ui(block: suspend CoroutineScope.() -> Unit) =
        CoroutineScope(ui).launch(block = block)
    
    fun default(block: suspend CoroutineScope.() -> Unit) =
        CoroutineScope(default).launch(block = block)

    fun CoroutineScope.io(block: suspend CoroutineScope.() -> Unit) =
        launch(io, block = block)

    fun CoroutineScope.ui(block: suspend CoroutineScope.() -> Unit) =
        launch(ui, block = block)
    
    fun CoroutineScope.default(block: suspend CoroutineScope.() -> Unit) =
        CoroutineScope(default).launch(block = block)

}