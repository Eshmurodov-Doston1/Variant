package uz.gxteam.variant.vm.statementVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.gxteam.variant.interceptor.MySharedPreference
import uz.gxteam.variant.models.getApplication.reqApplication.SendToken
import uz.gxteam.variant.models.messages.reqMessage.ReqMessage
import uz.gxteam.variant.models.sendMessage.sendMessage.SendMessageUser
import uz.gxteam.variant.repository.stateMent.StateMentRepository
import uz.gxteam.variant.resourse.applicationResourse.ApplicationResourse
import uz.gxteam.variant.resourse.broadCastAuth.BroadCastAuthResourse
import uz.gxteam.variant.resourse.message.AllMessageResourse
import uz.gxteam.variant.resourse.messageResourse.MessageResourse
import uz.gxteam.variant.resourse.stateMentApplications.ApplicationsResourse
import uz.gxteam.variant.resourse.uploadPhotos.UploadphotosResourse
import uz.gxteam.variant.socket.SendSocketData
import uz.gxteam.variant.utils.NetworkHelper
import javax.inject.Inject
@HiltViewModel
class StatementVm @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val stateMentRepository: StateMentRepository,
    private val mySharedPreference: MySharedPreference
) :ViewModel(){
    fun getAllApplications():StateFlow<ApplicationsResourse>{
        var applicaitons = MutableStateFlow<ApplicationsResourse>(ApplicationsResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                val allApplications = stateMentRepository.getAllApplications("${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                allApplications.catch {
                    applicaitons.emit(ApplicationsResourse.ErrorApplications(it.message, internetConnection = true))
                }.collect{
                    if (it.isSuccessful){
                        applicaitons.emit(ApplicationsResourse.SuccessApplications(it.body()))
                    }else{
                        applicaitons.emit(ApplicationsResourse.ErrorApplications(it.errorBody()?.string(), internetConnection = true, errorCode = it.code()))
                    }
                }

            }else{
                applicaitons.emit(ApplicationsResourse.ErrorApplications(internetConnection = false))
            }
        }
        return applicaitons
    }

    fun getApplication(sendToken: SendToken):StateFlow<ApplicationResourse>{
        var application = MutableStateFlow<ApplicationResourse>(ApplicationResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                var remoteApplication = stateMentRepository.getApplication(sendToken,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                remoteApplication.catch {
                    application.emit(ApplicationResourse.ErrorApplication(error = it.message, internetConnection = true))
                }.collect{
                   if(it.isSuccessful){
                       application.emit(ApplicationResourse.SuccessApplication(it.body()))
                   }else{
                       application.emit(ApplicationResourse.ErrorApplication(error = it.errorBody()?.string(), errorCode = it.code(), internetConnection = true))
                   }
                }
            }else{
                application.emit(ApplicationResourse.ErrorApplication(internetConnection = false))
            }
        }
        return application
    }


    fun getAllMessage(reqMessage: ReqMessage):StateFlow<AllMessageResourse>{
        var messages = MutableStateFlow<AllMessageResourse>(AllMessageResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                var remoteMessage = stateMentRepository.getAllMessage(reqMessage,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                remoteMessage.catch {
                    messages.emit(AllMessageResourse.ErrorAllMessage(error = it.message, internetConnection = true))
                }.collect{
                    if (it.isSuccessful){
                        messages.emit(AllMessageResourse.SuccessAllMessage(it.body()))
                    }else{
                        messages.emit(AllMessageResourse.ErrorAllMessage(error = it.errorBody()?.string(), errorCode = it.code(), internetConnection = true))
                    }
                }
            }else{
                messages.emit(AllMessageResourse.ErrorAllMessage(internetConnection = false))
            }
        }
        return messages
    }

    fun broadCastAuth(sendSocketData: SendSocketData):StateFlow<BroadCastAuthResourse>{
        var broadCastAuth = MutableStateFlow<BroadCastAuthResourse>(BroadCastAuthResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                val broadCastRes = stateMentRepository.broadCastingAuth(sendSocketData, "${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                broadCastRes.catch {
                    broadCastAuth.emit(BroadCastAuthResourse.ErrorBroadCast(error = it.message,internetConnection = true))
                }.collect{
                    if (it.isSuccessful){
                        broadCastAuth.emit(BroadCastAuthResourse.SuccessBroadCast(it.body()))
                    }else{
                        broadCastAuth.emit(BroadCastAuthResourse.ErrorBroadCast(error = it.errorBody()?.string(), errorCode = it.code(),internetConnection = true))
                    }
                }
            }else{
                broadCastAuth.emit(BroadCastAuthResourse.ErrorBroadCast(internetConnection = false))
            }
        }
        return broadCastAuth
    }



    fun sendMessage(sendMessageUser: SendMessageUser):StateFlow<MessageResourse>{
        var message = MutableStateFlow<MessageResourse>(MessageResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
               var remoteSendMessage = stateMentRepository.sendMessage(sendMessageUser,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                remoteSendMessage.catch {
                    message.emit(MessageResourse.ErrorMessage(it.message,true))
                }.collect{
                    if (it.isSuccessful){
                        message.emit(MessageResourse.SuccessMessage(it.body()))
                    }else{
                        message.emit(MessageResourse.ErrorMessage(error = it.errorBody()?.string(),errorCode = it.code(),internetConnection = true))
                    }
                }
            }else{
                message.emit(MessageResourse.ErrorMessage(internetConnection = false))
            }
        }
        return message
    }


    fun getUploadPhotos(sendToken: SendToken):StateFlow<UploadphotosResourse>{
        var uploadPhotos = MutableStateFlow<UploadphotosResourse>(UploadphotosResourse.Loading)
        viewModelScope.launch {
            if(networkHelper.isNetworkConnected()){
                stateMentRepository.getUploadPhotos(sendToken,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                    .catch {
                        uploadPhotos.emit(UploadphotosResourse.ErrorUploadPhotos(it.message,true))
                    }.collect{
                        if (it.isSuccessful){
                            uploadPhotos.emit(UploadphotosResourse.SuccessUploadPhotos(it.body()))
                        }else{
                            uploadPhotos.emit(UploadphotosResourse.ErrorUploadPhotos(error = it.errorBody()?.string(),errorCode = it.code(),internetConnection = true))
                        }
                    }
            }else{
                uploadPhotos.emit(UploadphotosResourse.ErrorUploadPhotos(internetConnection = false))
            }
        }
        return uploadPhotos
    }
}