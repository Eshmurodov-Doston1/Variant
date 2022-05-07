package uz.gxteam.variant.errors.errorInternet

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import uz.gxteam.variant.R
import uz.gxteam.variant.databinding.ErrorDialogBinding
import uz.gxteam.variant.databinding.NoInternetBinding
import uz.gxteam.variant.errors.authError.AuthErrors


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
    var str =""
    when(code){
        in 400..499 -> {
                authErrors.errors?.forEach {
                    str+="${it.message}\n"
                }
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
    var str =""
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