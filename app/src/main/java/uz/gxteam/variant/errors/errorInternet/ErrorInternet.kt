package uz.gxteam.variant.errors.errorInternet

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.google.gson.Gson
import com.google.gson.JsonArray
import org.json.JSONArray
import org.json.JSONObject
import uz.gxteam.variant.R
import uz.gxteam.variant.databinding.ErrorDialogBinding
import uz.gxteam.variant.databinding.NoInternetBinding
import uz.gxteam.variant.errors.authError.AuthErrors
import uz.gxteam.variant.errors.authError.Error
import uz.gxteam.variant.models.error.ErrorListAuth
import uz.gxteam.variant.models.error.NoErrorList
import uz.gxteam.variant.utils.AppConstant.ERRORCLIENT_END
import uz.gxteam.variant.utils.AppConstant.ERRORCLIENT_START
import uz.gxteam.variant.utils.AppConstant.ERRORSERVER_END
import uz.gxteam.variant.utils.AppConstant.ERRORSERVER_START
import uz.gxteam.variant.utils.AppConstant.ONE
import uz.gxteam.variant.utils.AppConstant.ZERO


fun noInternet(context:Context){
    var alertDailog = AlertDialog.Builder(context, R.style.BottomSheetDialogThem)
    val create = alertDailog.create()
    var noInternetBinding = NoInternetBinding.inflate(LayoutInflater.from(context),null,false)
    create.setView(noInternetBinding.root)
    noInternetBinding.okBtn.setOnClickListener {
        create.dismiss()
    }
    create.setCancelable(false)
    create.show()
}

fun authError(authErrors:String,context: Context,code:Int){
    var gson = Gson()
    var str =""
    var alertDailog = AlertDialog.Builder(context, R.style.BottomSheetDialogThem)
    val create = alertDailog.create()
    var errorDialogBinding = ErrorDialogBinding.inflate(LayoutInflater.from(context),null,false)
    when(code){
        in ERRORCLIENT_START..ERRORCLIENT_END -> {
            val fromJson = gson.fromJson(authErrors, AuthErrors::class.java)
            if (fromJson.errors.toString().subSequence(ZERO,ONE) == "["){
                val jsonArray = JSONObject(authErrors)
                val toString = jsonArray.get("errors").toString()
                val jsonArray1 = JSONArray(toString)
                for (i in ZERO until jsonArray1.length()){
                    var jsonObject = JSONObject(jsonArray1[i].toString())
                    str+=jsonObject.get("message")
                }
                errorDialogBinding.title.text = str
            }else{
                val error = gson.fromJson(authErrors, NoErrorList::class.java)
                errorDialogBinding.title.text = error.errors.message
            }
        }
        in ERRORSERVER_START..ERRORSERVER_END -> {
            errorDialogBinding.title.text =  context.getString(R.string.server_error)
        }
    }
      create.setView(errorDialogBinding.root)

    errorDialogBinding.okBtn.setOnClickListener {
        create.dismiss()
    }
    create.setCancelable(false)
    create.show()
}

fun errorNoClient(context: Context,code:Int){
    var str =""
    when(code){
        in ERRORSERVER_START..ERRORSERVER_END -> {
            str = context.getString(R.string.server_error)
        }
    }
    var alertDailog = AlertDialog.Builder(context, R.style.BottomSheetDialogThem)
    val create = alertDailog.create()
    var errorDialogBinding = ErrorDialogBinding.inflate(LayoutInflater.from(context),null,false)
    create.setView(errorDialogBinding.root)
    errorDialogBinding.title.text = str
    errorDialogBinding.okBtn.setOnClickListener {
        create.dismiss()
    }
    create.setCancelable(false)
    create.show()
}

fun messageError(message:String,context: Context){
    var alertDailog = AlertDialog.Builder(context, R.style.BottomSheetDialogThem)
    val create = alertDailog.create()
    var errorDialogBinding = ErrorDialogBinding.inflate(LayoutInflater.from(context),null,false)
    create.setView(errorDialogBinding.root)
    errorDialogBinding.title.text = message
    errorDialogBinding.okBtn.setOnClickListener {
        create.dismiss()
    }
    create.setCancelable(false)
    create.show()
}