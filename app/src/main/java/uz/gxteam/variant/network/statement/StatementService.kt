package uz.gxteam.variant.network.statement

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import uz.gxteam.variant.models.appliction.Application
import uz.gxteam.variant.models.getApplication.reqApplication.SendToken
import uz.gxteam.variant.models.getApplications.Applications
import uz.gxteam.variant.models.messages.reqMessage.ReqMessage
import uz.gxteam.variant.models.messages.resMessage.ResMessage
import uz.gxteam.variant.models.sendMessage.resMessage.ResMessageUser
import uz.gxteam.variant.models.sendMessage.sendMessage.SendMessageUser
import uz.gxteam.variant.models.uploadPhotos.UploadPhotos
import uz.gxteam.variant.socket.SendSocketData
import uz.gxteam.variant.socket.resSocet.ResSocket

interface StatementService {
    @POST("/api/chat/get/applications")
    suspend fun getApplications(
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json",
        @Header("Content-type") type: String = "application/json"
    ): Response<Applications>


    @POST("/api/chat/application")
    suspend fun getApplication(
        @Body sendToken: SendToken,
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"
    ):Response<Application>

    @POST("api/broadcasting/auth")
    suspend fun authBroadCasting(
        @Body sendSocketData: SendSocketData,
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"):Response<ResSocket>

    //AllMessage
    @POST("/api/chat/join")
    suspend fun getAllMessage(
        @Body reqMessage: ReqMessage,
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"
    ):Response<ResMessage>

    //sendMEssage
    @POST("/api/chat/send/message")
    suspend fun sendMessageChat(
        @Body sendMessageUser: SendMessageUser,
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"
    ):Response<ResMessageUser>

 //UploadImages
    @POST("/api/chat/get/photos")
    suspend fun getUploadPhotos(
     @Body sendToken: SendToken,
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json"
    ):Response<List<UploadPhotos>>



}