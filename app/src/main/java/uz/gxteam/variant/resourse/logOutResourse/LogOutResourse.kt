package uz.gxteam.variant.resourse.logOutResourse

import uz.gxteam.variant.models.auth.resAuth.ResAuth
import uz.gxteam.variant.models.getApplications.Applications
import uz.gxteam.variant.models.logOut.LogOut
import uz.gxteam.variant.resourse.authResourse.AuthResourse

sealed class LogOutResourse{
    object Loading: LogOutResourse()
    data class SuccessLogOut(var logOut:LogOut?): LogOutResourse()
    data class ErrorLogOut(var error:String?=null,var internetConnection:Boolean?=false,var errorCode:Int?=null): LogOutResourse()
}