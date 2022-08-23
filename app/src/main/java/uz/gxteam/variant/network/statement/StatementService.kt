package uz.gxteam.variant.network.statement

import retrofit2.Response
import retrofit2.http.*
import uz.gxteam.variant.models.appliction.Application
import uz.gxteam.variant.models.getApplication.reqApplication.SendToken
import uz.gxteam.variant.models.getApplications.Applications
import uz.gxteam.variant.models.messages.reqMessage.ReqMessage
import uz.gxteam.variant.models.messages.resMessage.ResMessage
import uz.gxteam.variant.models.sendMessage.resMessage.ResMessageUser
import uz.gxteam.variant.models.sendMessage.sendMessage.SendMessageUser
import uz.gxteam.variant.models.uploaCategory.UploadCategory
import uz.gxteam.variant.models.uploadPhotos.UploadPhotoData
import uz.gxteam.variant.models.uploadPhotos.UploadPhotos
import uz.gxteam.variant.socket.SendSocketData
import uz.gxteam.variant.socket.resSocet.ResSocket
import uz.gxteam.variant.utils.AppConstant.ACCEPT
import uz.gxteam.variant.utils.AppConstant.AUTH_STR
import uz.gxteam.variant.utils.AppConstant.HEADER_CONTENT
import uz.gxteam.variant.utils.AppConstant.TOKENTYPE
import uz.gxteam.variant.utils.AppConstant.TYPETOKEN

interface StatementService {
    @POST("/api/chat/get/applications")
    suspend fun getApplications(
        @Header(AUTH_STR) token: String,
        @Header(ACCEPT) accespt: String = TYPETOKEN,
        @Header(HEADER_CONTENT) type: String = TYPETOKEN
    ): Response<Applications>


    @POST("/api/chat/application")
    suspend fun getApplication(
        @Body sendToken: SendToken,
        @Header(AUTH_STR) token: String,
        @Header(ACCEPT) accespt: String = TYPETOKEN
    ):Response<Application>

    @POST("api/broadcasting/auth")
    suspend fun authBroadCasting(
        @Body sendSocketData: SendSocketData,
        @Header(AUTH_STR) token: String,
        @Header(ACCEPT) accespt: String = TYPETOKEN
    ):Response<ResSocket>

    //AllMessage
    @POST("/api/chat/join")
    suspend fun getAllMessage(
        @Body reqMessage: ReqMessage,
        @Header(AUTH_STR) token: String,
        @Header(ACCEPT) accespt: String = TYPETOKEN
    ):Response<ResMessage>

    //sendMEssage
    @POST("/api/chat/send/message")
    suspend fun sendMessageChat(
        @Body sendMessageUser: SendMessageUser,
        @Header(AUTH_STR) token: String,
        @Header(ACCEPT) accespt: String = TYPETOKEN
    ):Response<ResMessageUser>

 //UploadImages
    @POST("/api/chat/get/photos")
    suspend fun getUploadPhotos(
     @Body sendToken: SendToken,
     @Header(AUTH_STR) token: String,
     @Header(ACCEPT) accespt: String = TYPETOKEN
    ):Response<UploadPhotoData>

 //UploadImages
    @GET("/api/chat/get/status/{id}")
    suspend fun getUploadFileCategory(
     @Path("id") id:Int,
     @Header(AUTH_STR) token: String,
     @Header(ACCEPT) accespt: String = TYPETOKEN
    ):Response<UploadCategory>



}