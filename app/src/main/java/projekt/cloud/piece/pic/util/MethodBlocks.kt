package projekt.cloud.piece.pic.util

typealias RequestSuccessMethodBlock = () -> Unit
typealias RequestFailedMethodBlock = (Int) -> Unit

typealias CompleteCallback = (Int, String?) -> Unit