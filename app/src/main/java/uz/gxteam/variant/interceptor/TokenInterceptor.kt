package uz.gxteam.variant.interceptor

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.*
import org.json.JSONObject
import uz.gxteam.variant.BuildConfig
import uz.gxteam.variant.models.auth.resAuth.ResAuth
import uz.gxteam.variant.utils.AppConstant.ACCEPT
import uz.gxteam.variant.utils.AppConstant.AUTH_STR
import uz.gxteam.variant.utils.AppConstant.EMPTYTEXT
import uz.gxteam.variant.utils.AppConstant.REFRESHTOKENT_STR
import uz.gxteam.variant.utils.AppConstant.REFRESHTOKEN_ADD_URL
import uz.gxteam.variant.utils.AppConstant.SUCCESS_CODE
import uz.gxteam.variant.utils.AppConstant.TYPETOKEN
import javax.inject.Inject

/** TokentInternceptor this is class if responce code 401 refresh token send api
 *and update SharedPreference accessToken,refreshToken,tokenType and get apida data inn app **/

class TokenInterceptor @Inject constructor(
    private val mySharedPreference: MySharedPreference,
    @ApplicationContext private val context: Context) :Interceptor{

    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()
        val oldResponce = chain.proceed(oldRequest)
        val responseBody = oldResponce.body
        try {
            if (oldResponce.code==401){
                var modifiedRequest: Request?
                val client = OkHttpClient()
                val params = JSONObject()
                params.put(REFRESHTOKENT_STR, mySharedPreference.refreshToken ?: EMPTYTEXT)

                val body: RequestBody = RequestBody.create(responseBody?.contentType(),params.toString())

                val nRequest = Request.Builder()
                    .post(body)
                    .addHeader(ACCEPT,TYPETOKEN)
                    .url("${BuildConfig.BASE_URL}${REFRESHTOKEN_ADD_URL}")
                    .build()

                val response = client.newCall(nRequest).execute()

                if (response.code == SUCCESS_CODE) {
                    // Get response
                    val jsonData = response.body?.string() ?: EMPTYTEXT
                    val gson = Gson()
                    val resAuth: ResAuth = gson.fromJson(jsonData, ResAuth::class.java)

                    mySharedPreference.accessToken = resAuth.access_token
                    mySharedPreference.refreshToken = resAuth.refresh_token
                    mySharedPreference.tokenType = resAuth.token_type

                    oldResponce.close()
                    modifiedRequest = oldRequest.newBuilder()
                        .header(AUTH_STR, "${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                        .build()
                    return chain.proceed(modifiedRequest)
                }else{
                    mySharedPreference.clear()
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return oldResponce
    }
}
