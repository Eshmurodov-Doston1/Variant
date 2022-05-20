package uz.gxteam.variant.network.registerApi

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.gxteam.variant.models.auth.reqAuth.ReqAuth
import uz.gxteam.variant.models.auth.resAuth.ResAuth
import uz.gxteam.variant.models.logOut.LogOut
import uz.gxteam.variant.models.userData.UserData
import uz.gxteam.variant.utils.AppConstant.ACCEPT
import uz.gxteam.variant.utils.AppConstant.AUTH_STR
import uz.gxteam.variant.utils.AppConstant.HEADER_CONTENT
import uz.gxteam.variant.utils.AppConstant.TYPETOKEN

interface AuthService {
    @POST("/api/login")
    suspend fun login(@Body reqLogin: ReqAuth): Response<ResAuth>

    @GET("/api/user/detail")
    suspend fun getUserData(
        @Header(AUTH_STR) token: String,
        @Header(ACCEPT) accespt: String = TYPETOKEN,
        @Header(HEADER_CONTENT) type: String = TYPETOKEN
    ):Response<UserData>
    @POST("/api/logout")
    suspend fun logOut(
        @Header(AUTH_STR) token: String,
        @Header(ACCEPT) accespt: String = TYPETOKEN,
        @Header(HEADER_CONTENT) type: String = TYPETOKEN
    ):Response<LogOut>
}