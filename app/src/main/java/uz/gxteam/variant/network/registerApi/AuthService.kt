package uz.gxteam.variant.network.registerApi

import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.gxteam.variant.databinding.LogOutBinding
import uz.gxteam.variant.models.auth.reqAuth.ReqAuth
import uz.gxteam.variant.models.auth.resAuth.ResAuth
import uz.gxteam.variant.models.logOut.LogOut
import uz.gxteam.variant.models.userData.UserData

interface AuthService {
    @POST("/api/login")
    suspend fun login(@Body reqLogin: ReqAuth): Response<ResAuth>

    @GET("/api/user/detail")
    suspend fun getUserData(
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json",
        @Header("Content-type") type: String = "application/json"
    ):Response<UserData>
    @POST("/api/logout")
    suspend fun logOut(
        @Header("Authorization") token: String,
        @Header("Accept") accespt: String = "application/json",
        @Header("Content-type") type: String = "application/json"
    ):Response<LogOut>
}