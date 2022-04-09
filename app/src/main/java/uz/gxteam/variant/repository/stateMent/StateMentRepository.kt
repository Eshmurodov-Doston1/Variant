package uz.gxteam.variant.repository.stateMent

import kotlinx.coroutines.flow.flow
import uz.gxteam.variant.models.getApplication.reqApplication.SendToken
import uz.gxteam.variant.models.messages.reqMessage.ReqMessage
import uz.gxteam.variant.models.sendMessage.sendMessage.SendMessageUser
import uz.gxteam.variant.network.statement.StatementService
import uz.gxteam.variant.socket.SendSocketData
import javax.inject.Inject

class StateMentRepository @Inject constructor(
    private val statementService: StatementService
){
    suspend fun getAllApplications(token:String) = flow { emit(statementService.getApplications(token)) }

    suspend fun getApplication(sendToken: SendToken,token: String) = flow{ emit(statementService.getApplication(sendToken,token))}

    suspend fun broadCastingAuth(soketData: SendSocketData,token:String) = flow { emit(statementService.authBroadCasting(soketData,token)) }

    suspend fun getAllMessage(reqMessage: ReqMessage,token:String) = flow { emit(statementService.getAllMessage(reqMessage,token)) }

    suspend fun sendMessage(sendMessageUser: SendMessageUser,token:String) = flow { emit(statementService.sendMessageChat(sendMessageUser,token)) }

    suspend fun getUploadPhotos(sendToken:SendToken,token:String) = flow { emit(statementService.getUploadPhotos(sendToken,token)) }
}