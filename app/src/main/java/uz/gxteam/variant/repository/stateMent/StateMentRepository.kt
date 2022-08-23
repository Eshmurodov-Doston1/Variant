package uz.gxteam.variant.repository.stateMent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import uz.gxteam.variant.models.getApplication.reqApplication.SendToken
import uz.gxteam.variant.models.messages.reqMessage.ReqMessage
import uz.gxteam.variant.models.sendMessage.sendMessage.SendMessageUser
import uz.gxteam.variant.network.statement.StatementService
import uz.gxteam.variant.resourse.ResponseState
import uz.gxteam.variant.socket.SendSocketData
import uz.gxteam.variant.utils.base.ResponseFetcher
import java.io.IOException
import javax.inject.Inject

class StateMentRepository @Inject constructor(
    private val statementService: StatementService,
    private val responseFetcher: ResponseFetcher.Base,
){
    suspend fun getAllApplications(token:String) = responseFetcher.getFlowResponseState(statementService.getApplications(token))

    suspend fun getApplication(sendToken: SendToken,token: String) =responseFetcher.getFlowResponseState(statementService.getApplication(sendToken,token))

    suspend fun broadCastingAuth(soketData: SendSocketData,token:String) = responseFetcher.getFlowResponseState(statementService.authBroadCasting(soketData,token))

    suspend fun getAllMessage(reqMessage: ReqMessage,token:String) = responseFetcher.getFlowResponseState(statementService.getAllMessage(reqMessage,token))

    suspend fun sendMessage(sendMessageUser: SendMessageUser,token:String) = responseFetcher.getFlowResponseState(statementService.sendMessageChat(sendMessageUser,token))

    suspend fun getUploadPhotos(sendToken:SendToken,token:String) = responseFetcher.getFlowResponseState(statementService.getUploadPhotos(sendToken,token))

    suspend fun getUploadCategoryList(id:Int,token:String) = responseFetcher.getFlowResponseState(statementService.getUploadFileCategory(id,token))

    suspend fun getUploadData(
        sendToken: SendToken,
        token:String,
        id:Int
    ) = flow {
        val flow = try {
         coroutineScope {
             val listResponse = mutableListOf<Any>()
             var getApplication = async { statementService.getApplication(sendToken,token) }.await()
             var getUploadPhotos = async { statementService.getUploadPhotos(sendToken,token) }.await()
             var getUploadFileCategory = async { statementService.getUploadFileCategory(id,token) }.await()
             if (getApplication.isSuccessful) listResponse.add(getApplication.body() as Any)
             else throw HttpException(getApplication)
             if (getUploadPhotos.isSuccessful) listResponse.add(getUploadPhotos.body() as Any)
             else throw HttpException(getUploadPhotos)
             if (getUploadFileCategory.isSuccessful) listResponse.add(getUploadFileCategory.body() as Any)
             else throw HttpException(getUploadFileCategory)
             ResponseState.Success(listResponse)
         }
        }catch (e: IOException) {
            ResponseState.Error(e.hashCode(), e.message)
        } catch (e: HttpException) {
            ResponseState.Error(e.code(), getError(e.response()))
        }
        emit(flow)
    }.flowOn(Dispatchers.IO)


    fun <T> getError(response: Response<T>?): String {
        return try {
            val jsonObject = JSONObject(response?.errorBody()?.string().toString())
            var errorMessage = ""
            if (jsonObject.has("errors")){
                val jsonArray = jsonObject.getJSONArray("errors")
                if (jsonArray.length()==1){
                    errorMessage = jsonArray.getJSONObject(0).get("message").toString()
                }else{
                    (0..jsonArray.length()).onEach {
                        errorMessage += jsonArray.getJSONObject(it).get("message").toString()
                    }
                }
            }else if(jsonObject.has("message")) {
                errorMessage = jsonObject.get("message").toString()
            }
            return errorMessage
        }catch (e:Exception){
            e.message?:""
        }
    }
}