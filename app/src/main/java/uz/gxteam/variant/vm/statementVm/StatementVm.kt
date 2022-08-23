package uz.gxteam.variant.vm.statementVm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.gxteam.variant.interceptor.MySharedPreference
import uz.gxteam.variant.models.appliction.Application
import uz.gxteam.variant.models.getApplication.reqApplication.SendToken
import uz.gxteam.variant.models.getApplications.Applications
import uz.gxteam.variant.models.messages.reqMessage.ReqMessage
import uz.gxteam.variant.models.messages.resMessage.ResMessage
import uz.gxteam.variant.models.sendMessage.resMessage.ResMessageUser
import uz.gxteam.variant.models.sendMessage.sendMessage.SendMessageUser
import uz.gxteam.variant.models.uploaCategory.UploadCategory
import uz.gxteam.variant.models.uploadPhotos.UploadPhotoData
import uz.gxteam.variant.repository.stateMent.StateMentRepository
import uz.gxteam.variant.resourse.ResponseState
import uz.gxteam.variant.socket.SendSocketData
import uz.gxteam.variant.socket.resSocet.ResSocket
import uz.gxteam.variant.utils.AppConstant.NO_INTERNET
import uz.gxteam.variant.utils.NetworkHelper
import javax.inject.Inject

@HiltViewModel
class StatementVm @Inject constructor(
    private val networkHelper: NetworkHelper,
    private val stateMentRepository: StateMentRepository,
    private val mySharedPreference: MySharedPreference
) :ViewModel(){

    // TODO: getUploadCategory
    val getUploadCategory:StateFlow<ResponseState<UploadCategory?>> get() = _getUploadCategory
    private var _getUploadCategory = MutableStateFlow<ResponseState<UploadCategory?>>(ResponseState.Loading)
    // TODO: getAllApplications
    val getAllApplications:StateFlow<ResponseState<Applications?>> get() = _getAllApplications
    private var _getAllApplications = MutableStateFlow<ResponseState<Applications?>>(ResponseState.Loading)
    // TODO: getApplication
    val getApplication:StateFlow<ResponseState<Application?>> get() = _getApplication
    private var _getApplication = MutableStateFlow<ResponseState<Application?>>(ResponseState.Loading)
    // TODO: getAllMessage
    val getAllMessage:StateFlow<ResponseState<ResMessage?>> get() = _getAllMessage
    private var _getAllMessage = MutableStateFlow<ResponseState<ResMessage?>>(ResponseState.Loading)
    // TODO: broadCastAuth
    val broadCastAuth:StateFlow<ResponseState<ResSocket?>> get() = _broadCastAuth
    private var _broadCastAuth = MutableStateFlow<ResponseState<ResSocket?>>(ResponseState.Loading)
    // TODO: sendMessage
    val sendMessage:StateFlow<ResponseState<ResMessageUser?>> get() = _sendMessage
    private var _sendMessage = MutableStateFlow<ResponseState<ResMessageUser?>>(ResponseState.Loading)
    // TODO: getUploadPhotos
    val getUploadPhotos:StateFlow<ResponseState<UploadPhotoData?>> get() = _getUploadPhotos
    private var _getUploadPhotos = MutableStateFlow<ResponseState<UploadPhotoData>>(ResponseState.Loading)

    // TODO: UploadFile data
    val uploadFileData:StateFlow<ResponseState<List<Any>?>> get() = _uploadFileData
    private var _uploadFileData = MutableStateFlow<ResponseState<List<Any>>>(ResponseState.Loading)

    fun getUploadCategory(id:Int) =  viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                _getUploadCategory.emit(ResponseState.Loading)
                try {
                    stateMentRepository.getUploadCategoryList(id,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                   .collect{ response-> _getUploadCategory.emit(response) }
                }catch (e:Exception){
                    _getUploadCategory.emit(ResponseState.Error(e.hashCode(),e.message))
                }
            }else{
                _getUploadCategory.emit(ResponseState.Error(NO_INTERNET))
            }
        }

    fun getAllApplications() = viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                _getAllApplications.emit(ResponseState.Loading)
                try {
                    stateMentRepository.getAllApplications("${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                    .collect{response-> _getAllApplications.emit(response) }
                }catch (e:Exception){
                    _getAllApplications.emit(ResponseState.Error(e.hashCode(),e.message))
                }
            }else{
                _getAllApplications.emit(ResponseState.Error(NO_INTERNET))
            }
        }

    fun getApplication(sendToken: SendToken) = viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                _getApplication.emit(ResponseState.Loading)
                try{
                    stateMentRepository.getApplication(sendToken,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                    .collect{ response-> _getApplication.emit(response) }
                }catch (e:Exception){
                    _getApplication.emit(ResponseState.Error(e.hashCode(),e.message))
                }

            }else{
                _getApplication.emit(ResponseState.Error(NO_INTERNET))
            }
        }


    fun getAllMessage(reqMessage: ReqMessage) = viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                _getAllMessage.emit(ResponseState.Loading)
                try {
                    stateMentRepository.getAllMessage(reqMessage,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                   .collect{ response-> _getAllMessage.emit(response) }
                }catch (e:Exception){
                    _getAllMessage.emit(ResponseState.Error(e.hashCode(), e.message))
                }
            }else{
                _getAllMessage.emit(ResponseState.Error(NO_INTERNET))
            }
        }

    fun broadCastAuth(sendSocketData: SendSocketData) = viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                _broadCastAuth.emit(ResponseState.Loading)
                try {
                   stateMentRepository.broadCastingAuth(sendSocketData, "${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                    .collect{ response-> _broadCastAuth.emit(response) }
                }catch (e:Exception){
                    _broadCastAuth.emit(ResponseState.Error(e.hashCode(),e.message))
                }
            }else{
                _broadCastAuth.emit(ResponseState.Error(NO_INTERNET))
            }
        }


    fun sendMessage(sendMessageUser: SendMessageUser) = viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                try {
                    stateMentRepository.sendMessage(sendMessageUser,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                    .collect{response->
                       _sendMessage.emit(response)
                    }
                }catch (e:Exception){
                    _sendMessage.emit(ResponseState.Error(e.hashCode(),e.message))
                }

            }else{
                _sendMessage.emit(ResponseState.Error(NO_INTERNET))
            }
        }



    fun getUploadPhotos(sendToken: SendToken) = viewModelScope.launch {
            if(networkHelper.isNetworkConnected()){
                _getUploadPhotos.emit(ResponseState.Loading)
                try {
                    stateMentRepository.getUploadPhotos(sendToken,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                        .collect{ response-> _getUploadPhotos.emit(response as ResponseState<UploadPhotoData>) }
                }catch (e:Exception){
                    _getUploadPhotos.emit(ResponseState.Error(e.hashCode(),e.message))
                }

            }else{
                _getUploadPhotos.emit(ResponseState.Error(NO_INTERNET))
            }
        }


    fun getUploadData(sendToken: SendToken,id: Int) = viewModelScope.launch {
        if(networkHelper.isNetworkConnected()){
            _uploadFileData.emit(ResponseState.Loading)
           try {
               stateMentRepository.getUploadData(sendToken,"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}",id)
                   .collect{response-> _uploadFileData.emit(response)}
           }catch (e:Exception){
               _uploadFileData.emit(ResponseState.Error(e.hashCode(),e.message))
           }
        }else{
            _uploadFileData.emit(ResponseState.Error(NO_INTERNET))
        }
    }

    fun getShared() =  mySharedPreference
}