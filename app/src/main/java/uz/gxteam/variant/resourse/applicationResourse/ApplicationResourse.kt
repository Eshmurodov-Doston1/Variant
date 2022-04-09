package uz.gxteam.variant.resourse.applicationResourse

import uz.gxteam.variant.models.appliction.Application
import uz.gxteam.variant.models.auth.resAuth.ResAuth
import uz.gxteam.variant.models.getApplications.Applications

sealed class ApplicationResourse{
    object Loading:ApplicationResourse()
    data class SuccessApplication(var application: Application?):ApplicationResourse()
    data class ErrorApplication(var error:String?=null,var internetConnection:Boolean?=false,var errorCode:Int?=null):ApplicationResourse()
}