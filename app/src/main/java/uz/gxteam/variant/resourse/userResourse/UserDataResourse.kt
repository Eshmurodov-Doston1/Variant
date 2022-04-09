package uz.gxteam.variant.resourse.userResourse

import uz.gxteam.variant.database.entity.userData.UserDataEntity
import uz.gxteam.variant.models.getApplications.Applications
import uz.gxteam.variant.models.messages.resMessage.ResMessage
import uz.gxteam.variant.models.userData.UserData
import uz.gxteam.variant.resourse.stateMentApplications.ApplicationsResourse

sealed class UserDataResourse {
    object Loading: UserDataResourse()
    data class SuccessUserResourse(var userData:UserData?): UserDataResourse()
    data class ErrorUserResourse(var error:String?=null,var internetConnection:Boolean?=false,var errorCode:Int?=null): UserDataResourse()
}