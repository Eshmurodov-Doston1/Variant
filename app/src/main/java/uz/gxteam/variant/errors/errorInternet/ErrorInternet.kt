package uz.gxteam.variant.errors.errorInternet

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import uz.gxteam.variant.R
import uz.gxteam.variant.databinding.ErrorDialogBinding
import uz.gxteam.variant.databinding.NoInternetBinding
import uz.gxteam.variant.errors.authError.AuthErrors
import uz.gxteam.variant.utils.AppConstant.ERRORCLIENT_END
import uz.gxteam.variant.utils.AppConstant.ERRORCLIENT_START
import uz.gxteam.variant.utils.AppConstant.ERRORSERVER_END
import uz.gxteam.variant.utils.AppConstant.ERRORSERVER_START


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
        in ERRORCLIENT_START..ERRORCLIENT_END -> {
                authErrors.errors?.forEach {
                    str+="${it.message}\n"
                }
        }
        in ERRORSERVER_START..ERRORSERVER_END -> {
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
        in ERRORSERVER_START..ERRORSERVER_END -> {
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