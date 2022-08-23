package uz.gxteam.variant.resourse


sealed class ResponseState<out T> {
    object Loading:ResponseState<Nothing>()
    data class Success<T>(val data:T):ResponseState<T>()
    data class Error(val errorCode:Int?=null,val errorMessage:String?=null):ResponseState<Nothing>()
}