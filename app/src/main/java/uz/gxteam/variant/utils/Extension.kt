package uz.gxteam.variant.utils

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.StateFlow
import uz.gxteam.variant.resourse.ResponseState
import uz.gxteam.variant.utils.AppConstant.ZERO
import uz.gxteam.variant.utils.uiController.UiController

suspend inline fun <reified T> StateFlow<ResponseState<T>>.fetchResult(
    uiController: UiController,
    crossinline invokeSuccess: (T) -> Unit,
    noinline onClick:(isClick:Boolean)->Unit
) {
    var count = ZERO
    this.collect { result ->
        when (result) {
            is ResponseState.Loading -> {
                uiController.showProgress()
            }
            is ResponseState.Success -> {
                uiController.hideProgress()
                val parseData = result.data?.parseJsonInClass(T::class.java)
                invokeSuccess.invoke(parseData!!)
            }
            is ResponseState.Error -> {
                uiController.hideProgress()
                if (count == ZERO){
                    uiController.error(result.errorCode?.toLong()?:0L, result.errorMessage.toString(),onClick)
                    count++
                }
            }
        }
    }
}


inline fun <reified T> Any.parseJsonInClass(classData:Class<T>):T{
    val gson = GsonBuilder()
    val toJson = gson.create().toJson(this)
    return gson.create().fromJson(toJson.toString(),classData)
}


fun View.visible(){
    this.isVisible = true
}
fun View.gone(){
    this.isVisible = false
}


fun Button.visible(){
    this.isVisible = true
}

fun Button.gone(){
    this.isVisible = false
}

fun Button.enabledTrue(){
    this.isEnabled = true
}

fun Button.enabledFalse(){
    this.isEnabled = false
}
fun TextView.textApp(str:String){
    this.text = str
}