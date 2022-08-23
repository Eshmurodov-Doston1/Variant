package uz.gxteam.variant.repository.authRepository

import uz.gxteam.variant.models.auth.reqAuth.ReqAuth
import uz.gxteam.variant.network.registerApi.AuthService
import uz.gxteam.variant.utils.base.ResponseFetcher
import javax.inject.Inject

class AuhtRepository @Inject constructor(
    private val authService: AuthService,
    private val responseFetcher: ResponseFetcher.Base,
) {
    suspend fun authVariant(reqAuth: ReqAuth) = responseFetcher.getFlowResponseState(authService.login(reqAuth))

    suspend fun userData(token:String) = responseFetcher.getFlowResponseState(authService.getUserData(token))

    suspend fun logOut(token:String) = responseFetcher.getFlowResponseState(authService.logOut(token))
}