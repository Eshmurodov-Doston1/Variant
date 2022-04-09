package uz.gxteam.variant.vm.authViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import uz.gxteam.variant.database.entity.userData.UserDataEntity
import uz.gxteam.variant.interceptor.MySharedPreference
import uz.gxteam.variant.models.auth.reqAuth.ReqAuth
import uz.gxteam.variant.network.registerApi.AuthService
import uz.gxteam.variant.repository.authRepository.AuhtRepository
import uz.gxteam.variant.resourse.authResourse.AuthResourse
import uz.gxteam.variant.resourse.logOutResourse.LogOutResourse
import uz.gxteam.variant.resourse.userResourse.UserDataResourse
import uz.gxteam.variant.utils.NetworkHelper
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuhtRepository,
    private val networkHelper: NetworkHelper,
    private val mySharedPreference: MySharedPreference
):ViewModel() {
    fun authApp(reqAuth: ReqAuth):StateFlow<AuthResourse>{
        var auth = MutableStateFlow<AuthResourse>(AuthResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                var remoteAuth = authRepository.authVariant(reqAuth)
                remoteAuth.catch {
                    auth.emit(AuthResourse.ErrorAuth(error = it.message,internetConnection = true))
                }.collect {
                    if (it.isSuccessful){
                        auth.emit(AuthResourse.SuccessAuth(it.body()))
                        mySharedPreference.accessToken = it.body()?.access_token
                        mySharedPreference.refreshToken = it.body()?.refresh_token
                        mySharedPreference.tokenType = it.body()?.token_type
                    }else{
                        auth.emit(AuthResourse.ErrorAuth(error = it.errorBody()?.string(),internetConnection = true, errorCode = it.code()))
                    }
                }
            }else{
                auth.emit(AuthResourse.ErrorAuth(internetConnection = false))
            }
        }
        return auth
    }

    fun getSharedPreference():MySharedPreference{
        return mySharedPreference
    }

    fun getUserData():StateFlow<UserDataResourse>{
        var userData = MutableStateFlow<UserDataResourse>(UserDataResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
                var remoteUser = authRepository.userData("${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                remoteUser.catch {
                    userData.emit(UserDataResourse.ErrorUserResourse(error = it.message,internetConnection = true))
                }.collect{
                    if (it.isSuccessful){
                        it.body().let {
                            userData.emit(UserDataResourse.SuccessUserResourse(it))
                        }
                    }else{
                        userData.emit(UserDataResourse.ErrorUserResourse(error = it.errorBody()?.string(),internetConnection = true, errorCode = it.code()))
                    }
                }
            }else{
                userData.emit(UserDataResourse.ErrorUserResourse(internetConnection = false))
            }
        }
        return userData
    }

    fun getUserdatabase():AuhtRepository{
        return authRepository
    }

    fun logOut():StateFlow<LogOutResourse>{
        var logOut = MutableStateFlow<LogOutResourse>(LogOutResourse.Loading)
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()){
               var remoteLogout = authRepository.logOut("${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                remoteLogout.catch {
                    logOut.emit(LogOutResourse.ErrorLogOut(error = it.message,internetConnection = true))
                }.collect{
                    if (it.isSuccessful){
                        logOut.emit(LogOutResourse.SuccessLogOut(it.body()))
                    }else{
                        logOut.emit(LogOutResourse.ErrorLogOut(error = it.errorBody()?.string(),internetConnection = true, errorCode = it.code()))
                    }
                }
            }else{
                logOut.emit(LogOutResourse.ErrorLogOut(internetConnection = false))
            }
        }
        return logOut
    }
}