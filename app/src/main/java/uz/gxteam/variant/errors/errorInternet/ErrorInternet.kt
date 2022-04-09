package uz.gxteam.variant.errors.errorInternet

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import com.google.gson.Gson
import uz.gxteam.variant.R
import uz.gxteam.variant.databinding.ErrorDialogBinding
import uz.gxteam.variant.databinding.NoInternetBinding
import uz.gxteam.variant.errors.authError.AuthErrors
import uz.gxteam.variant.errors.authError.Error


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

fun authError(authErrors:AuthErrors,context: Context,code:Int){
    var str:String=""
    when(code){
        in 400..499 -> {
//            if (authErrors.errorsApp!=null){
//                str+=authErrors.errorsApp?.message
//            }else{

            Log.e("Error", authErrors.errors.toString() )
                authErrors.errors?.forEach {
                    str+="${it.message}\n"
                }
           // }

        }
        in 500..599 -> {
           str = context.getString(R.string.server_error)
        }
    }
    var alertDailog = AlertDialog.Builder(context, R.style.BottomSheetDialogThem)
    val create = alertDailog.create()
    var errorDialogBinding = ErrorDialogBinding.inflate(LayoutInflater.from(context),null,false)
    create.setView(errorDialogBinding.root)
    errorDialogBinding.errorText.text = str
    errorDialogBinding.okBtn.setOnClickListener {
        create.dismiss()
    }
    create.setCancelable(false)
    create.show()
}

fun errorNoClient(context: Context,code:Int){
    var str:String=""
    when(code){
        in 500..599 -> {
            str = context.getString(R.string.server_error)
        }
    }
    var alertDailog = AlertDialog.Builder(context, R.style.BottomSheetDialogThem)
    val create = alertDailog.create()
    var errorDialogBinding = ErrorDialogBinding.inflate(LayoutInflater.from(context),null,false)
    create.setView(errorDialogBinding.root)
    errorDialogBinding.errorText.text = str
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
    errorDialogBinding.errorText.text = message
    errorDialogBinding.okBtn.setOnClickListener {
        create.dismiss()
    }
    create.setCancelable(false)
    create.show()
}