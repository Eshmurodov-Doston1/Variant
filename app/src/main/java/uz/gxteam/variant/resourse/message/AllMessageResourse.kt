package uz.gxteam.variant.resourse.message

import uz.gxteam.variant.models.getApplications.Applications
import uz.gxteam.variant.models.messages.resMessage.ResMessage
import uz.gxteam.variant.resourse.stateMentApplications.ApplicationsResourse

sealed class AllMessageResourse {
    object Loading: AllMessageResourse()
    data class SuccessAllMessage(var resMessage:ResMessage?): AllMessageResourse()
    data class ErrorAllMessage(var error:String?=null,var internetConnection:Boolean?=false,var errorCode:Int?=null): AllMessageResourse()
}