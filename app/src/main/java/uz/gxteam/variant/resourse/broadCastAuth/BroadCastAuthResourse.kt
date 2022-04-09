package uz.gxteam.variant.resourse.broadCastAuth

import uz.gxteam.variant.models.auth.resAuth.ResAuth
import uz.gxteam.variant.models.getApplications.Applications
import uz.gxteam.variant.resourse.authResourse.AuthResourse
import uz.gxteam.variant.socket.resSocet.ResSocket

sealed class BroadCastAuthResourse{
    object Loading: BroadCastAuthResourse()
    data class SuccessBroadCast(var resSocket: ResSocket?): BroadCastAuthResourse()
    data class ErrorBroadCast(var error:String?=null,var internetConnection:Boolean?=false,var errorCode:Int?=null): BroadCastAuthResourse()
}