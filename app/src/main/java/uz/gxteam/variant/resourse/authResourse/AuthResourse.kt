package uz.gxteam.variant.resourse.authResourse

import uz.gxteam.variant.models.auth.resAuth.ResAuth

sealed class AuthResourse{
    object Loading:AuthResourse()
    data class SuccessAuth(var resAuth: ResAuth?):AuthResourse()
    data class ErrorAuth(var error:String?=null,var internetConnection:Boolean?=false,var errorCode:Int?=null):AuthResourse()
}