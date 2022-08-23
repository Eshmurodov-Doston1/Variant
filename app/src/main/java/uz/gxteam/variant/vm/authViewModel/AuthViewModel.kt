package uz.gxteam.variant.vm.authViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import uz.gxteam.variant.interceptor.MySharedPreference
import uz.gxteam.variant.models.auth.reqAuth.ReqAuth
import uz.gxteam.variant.models.auth.resAuth.ResAuth
import uz.gxteam.variant.models.logOut.LogOut
import uz.gxteam.variant.repository.authRepository.AuhtRepository
import uz.gxteam.variant.resourse.ResponseState
import uz.gxteam.variant.utils.AppConstant.NO_INTERNET
import uz.gxteam.variant.utils.NetworkHelper
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuhtRepository,
    private val networkHelper: NetworkHelper,
    private val mySharedPreference: MySharedPreference
):ViewModel() {
    val mySharedPreferenceApp get() = mySharedPreference
    // TODO: auth Variant
    val authVariant:StateFlow<ResponseState<ResAuth?>> get() = _authVariant
    private var _authVariant = MutableStateFlow<ResponseState<ResAuth?>>(ResponseState.Loading)


    // TODO: User Data
    val userData:StateFlow<ResponseState<uz.gxteam.variant.models.userData.UserData?>> get() = _userData
    private var _userData = MutableStateFlow<ResponseState<uz.gxteam.variant.models.userData.UserData?>>(ResponseState.Loading)

    // TODO: LogOut
    val logOut:StateFlow<ResponseState<LogOut?>> get() = _logOut
    private var _logOut = MutableStateFlow<ResponseState<LogOut?>>(ResponseState.Loading)


    fun authApp(reqAuth: ReqAuth) = viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                _authVariant.emit(ResponseState.Loading)
                try {
                    authRepository.authVariant(reqAuth).collect { response->
                        _authVariant.emit(response)
                    }
                }catch (e:IOException){
                    _authVariant.emit(ResponseState.Error(e.hashCode(),e.message))
                }catch (e:HttpException){
                    _authVariant.emit(ResponseState.Error( e.hashCode(),e.message))
                }catch (e:Exception){
                    _authVariant.emit(ResponseState.Error( hashCode(), e.message))
                }
            }else{
                _authVariant.emit(ResponseState.Error(NO_INTERNET))
            }
        }


    fun getSharedPreference():MySharedPreference{
        return mySharedPreference
    }

    fun getUserData() = viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                try {
                  authRepository.userData("${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                        .collect{ response-> _userData.emit(response) }
                }catch (e:Exception){
                    _userData.emit(ResponseState.Error(e.hashCode(),e.message))
                }


            }else{
                _userData.emit(ResponseState.Error(NO_INTERNET))
            }
        }


    fun logOut() = viewModelScope.launch {
                if (networkHelper.isNetworkConnected()){
                    try {
                        authRepository.logOut("${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                            .collect{response->
                                _logOut.emit(response)
                            }
                    }catch (e:Exception){
                        _logOut.emit(ResponseState.Error(e.hashCode(),e.message))
                    }
                }else{
                    _logOut.emit(ResponseState.Error(NO_INTERNET))
                }


        }
}