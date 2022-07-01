package uz.gxteam.variant.vm.authViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import uz.gxteam.variant.interceptor.MySharedPreference
import uz.gxteam.variant.models.auth.reqAuth.ReqAuth
import uz.gxteam.variant.repository.authRepository.AuhtRepository
import uz.gxteam.variant.resourse.authResourse.AuthResourse
import uz.gxteam.variant.resourse.logOutResourse.LogOutResourse
import uz.gxteam.variant.resourse.userResourse.UserDataResourse
import uz.gxteam.variant.utils.NetworkHelper
import javax.inject.Inject

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
                try {
                    authRepository.authVariant(reqAuth).catch {
                        auth.emit(AuthResourse.ErrorAuth(error = it.message,internetConnection = true))
                    }.collect {
                        if (it.isSuccessful){
                            auth.emit(AuthResourse.SuccessAuth(it.body()))
                            mySharedPreference.accessToken = it.body()?.access_token
                            mySharedPreference.refreshToken = it.body()?.refresh_token
                            mySharedPreference.tokenType = it.body()?.token_type
                        }else{
                            auth.emit(AuthResourse.ErrorAuth(error = it.errorBody()?.string().toString(),internetConnection = true, errorCode = it.code()))
                        }
                    }
                }catch (e:IOException){
                    auth.emit(AuthResourse.ErrorAuth(internetConnection = false))
                }catch (e:HttpException){
                    auth.emit(AuthResourse.ErrorAuth(error = e.message,internetConnection = true, errorCode = e.code()))
                }catch (e:Exception){
                    auth.emit(AuthResourse.ErrorAuth(error = e.message,internetConnection = true, errorCode = hashCode()))
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
                try {
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
                }catch (e:HttpException){
                    userData.emit(UserDataResourse.ErrorUserResourse(error = e.message,internetConnection = true, errorCode = e.code()))
                }catch (e:Exception){
                    userData.emit(UserDataResourse.ErrorUserResourse(error = e.message,internetConnection = true, errorCode = e.hashCode()))
                }


            }else{
                userData.emit(UserDataResourse.ErrorUserResourse(internetConnection = false))
            }
        }
        return userData
    }


    fun logOut():StateFlow<LogOutResourse>{
        var logOut = MutableStateFlow<LogOutResourse>(LogOutResourse.Loading)
        viewModelScope.launch {
            try {

                if (networkHelper.isNetworkConnected()){
                    var remoteLogout = authRepository.logOut("${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                    remoteLogout.catch {
                        logOut.emit(LogOutResourse.ErrorLogOut(error = it.message,internetConnection = true))
                    }.collect{
                        if (it.isSuccessful){
                            mySharedPreference.clear()
                            logOut.emit(LogOutResourse.SuccessLogOut(it.body()))
                        }else{
                            logOut.emit(LogOutResourse.ErrorLogOut(error = it.errorBody()?.string(),internetConnection = true, errorCode = it.code()))
                        }
                    }
                }else{
                    logOut.emit(LogOutResourse.ErrorLogOut(internetConnection = false))
                }
            }catch (e:HttpException){
                logOut.emit(LogOutResourse.ErrorLogOut(error = e.message,internetConnection = true, errorCode = e.code()))
            }catch (e:Exception){
                logOut.emit(LogOutResourse.ErrorLogOut(error = e.message,internetConnection = true, errorCode = e.hashCode()))
            }

        }
        return logOut
    }
}