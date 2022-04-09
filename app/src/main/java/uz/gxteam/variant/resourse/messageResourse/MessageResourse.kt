package uz.gxteam.variant.resourse.messageResourse

import uz.gxteam.variant.models.messages.resMessage.ResMessage
import uz.gxteam.variant.models.sendMessage.resMessage.ResMessageUser
import uz.gxteam.variant.resourse.message.AllMessageResourse

sealed class MessageResourse {
    object Loading: MessageResourse()
    data class SuccessMessage(var resMessageUser: ResMessageUser?): MessageResourse()
    data class ErrorMessage(var error:String?=null,var internetConnection:Boolean?=false,var errorCode:Int?=null): MessageResourse()
}