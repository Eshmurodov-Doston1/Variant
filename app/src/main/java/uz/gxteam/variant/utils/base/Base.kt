package uz.gxteam.variant.utils.base

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import uz.gxteam.variant.resourse.ResponseState
import java.io.IOException
import javax.inject.Inject


interface ResponseFetcher{
    fun <T> getFlowResponseState(response: Response<T>?): Flow<ResponseState<T?>>
    fun <T> getError(response: Response<T>?):String
    class Base @Inject constructor():ResponseFetcher{
        override fun <T> getFlowResponseState(response: Response<T>?) = flow {
            var flow = try {
                coroutineScope {
                    if (response?.isSuccessful == true) ResponseState.Success(response.body())
                    else throw HttpException(response)
                }
            }catch (e:IOException){
                ResponseState.Error(e.hashCode(),e.message)
            }catch (e:HttpException){
                ResponseState.Error(e.code(),getError(e.response()))
            } catch (e:Exception){
                ResponseState.Error(e.hashCode(),e.message)
            }
            emit(flow)
        }.flowOn(Dispatchers.IO)

        override fun <T> getError(response: Response<T>?): String {
          return try {
               val jsonObject = JSONObject(response?.errorBody()?.string().toString())
              var errorMessage = ""
              if (jsonObject.has("errors")){
                  val jsonArray = jsonObject.getJSONArray("errors")
                  if (jsonArray.length()==1){
                      errorMessage = jsonArray.getJSONObject(0).get("message").toString()
                  }else{
                      (0..jsonArray.length()).onEach {
                          errorMessage += jsonArray.getJSONObject(it).get("message").toString()
                      }
                  }
              }else if(jsonObject.has("message")) {
                  errorMessage = jsonObject.get("message").toString()
              }
               return errorMessage
           }catch (e:Exception){
                e.message?:""
           }
        }

    }
}