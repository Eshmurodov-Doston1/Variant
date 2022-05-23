package uz.gxteam.variant.repository.authRepository

import kotlinx.coroutines.flow.flow
import uz.gxteam.variant.models.auth.reqAuth.ReqAuth
import uz.gxteam.variant.network.registerApi.AuthService
import javax.inject.Inject

class AuhtRepository @Inject constructor(
    private val authService: AuthService,
) {
    suspend fun authVariant(reqAuth: ReqAuth) = flow {
        emit(authService.login(reqAuth))
    }

    suspend fun userData(token:String) = flow { emit(authService.getUserData(token)) }

    suspend fun logOut(token:String) = flow { emit(authService.logOut(token)) }
}