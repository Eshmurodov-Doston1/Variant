package uz.gxteam.variant.interceptor

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.*
import org.json.JSONObject
import uz.gxteam.variant.BuildConfig
import uz.gxteam.variant.models.auth.resAuth.ResAuth
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
            Log.e("Response",oldResponce.code.toString())
            Log.e("Response",oldResponce.body.toString())

            if (oldResponce.code==401){
                var modifiedRequest: Request?
                val client = OkHttpClient()
                val params = JSONObject()
                params.put("refresh_token", mySharedPreference.refreshToken ?: "")
                Log.e("refreshToken", mySharedPreference.refreshToken.toString())

                val string = params.getString("refresh_token")
                Log.e("Put", string)

                val body: RequestBody = RequestBody.create(responseBody?.contentType(),params.toString())

                val nRequest = Request.Builder()
                    .post(body)
                    .addHeader("Accept","application/json")
                    .url("${BuildConfig.BASE_URL}/api/refresh/token")
                    .build()

                val response = client.newCall(nRequest).execute()

                Log.e("Code", response.code.toString())
                if (response.code == 200) {
                    // Get response
                    val jsonData = response.body?.string() ?: ""
                    val gson = Gson()
                    val resAuth: ResAuth = gson.fromJson(jsonData, ResAuth::class.java)
                    Log.e("RefreshData", resAuth.toString())

                    mySharedPreference.accessToken = resAuth.access_token
                    mySharedPreference.refreshToken = resAuth.refresh_token
                    mySharedPreference.tokenType = resAuth.token_type

                    oldResponce.close()
                    modifiedRequest = oldRequest.newBuilder()
                        .header("Authorization", "${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                        .build()
                    return chain.proceed(modifiedRequest)
                }else{
                    mySharedPreference.clear()
//                val popUpTo = NavOptions.Builder()
//                    .setPopUpTo(R.id.authFragment, true)
//                NavController(context).navigate(R.id.authFragment,null,popUpTo.build())
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return oldResponce
    }
}
