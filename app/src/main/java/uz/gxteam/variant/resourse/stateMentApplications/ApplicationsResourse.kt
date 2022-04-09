package uz.gxteam.variant.resourse.stateMentApplications

import uz.gxteam.variant.models.auth.resAuth.ResAuth
import uz.gxteam.variant.models.getApplications.Applications
import uz.gxteam.variant.resourse.authResourse.AuthResourse

sealed class ApplicationsResourse{
    object Loading: ApplicationsResourse()
    data class SuccessApplications(var applications: Applications?): ApplicationsResourse()
    data class ErrorApplications(var error:String?=null,var internetConnection:Boolean?=false,var errorCode:Int?=null): ApplicationsResourse()
}