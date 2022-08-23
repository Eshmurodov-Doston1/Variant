package uz.gxteam.variant.utils.container

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.gson.Gson
import uz.gxteam.variant.R
import uz.gxteam.variant.databinding.LoadingViewBinding
import uz.gxteam.variant.databinding.UploadLoadingBinding
import uz.gxteam.variant.interceptor.MySharedPreference
import uz.gxteam.variant.utils.AppConstant
import uz.gxteam.variant.utils.uiController.UiController

class AppCompositionRoot(
    private val activity:AppCompatActivity,
    private val navController: NavController,
    private val uiController: UiController,
    private val mySharedPreferences: MySharedPreference,
) {
    private val mLayoutInflater: LayoutInflater get() = activity.layoutInflater
    val mActivity: AppCompatActivity get() = activity
    private val gson = Gson()
    private val dialogHelper:DialogHelper by lazy {
        DialogHelper(mContext,mLayoutInflater,activity,navController)
    }

    val mLifecycleOwner: LifecycleOwner get() = activity
    val mContext: Context get() = activity
    val mNavController: NavController get() = navController

    val uiControllerApp get() = uiController
    val mySharedPreferencesApp get() = mySharedPreferences


    var listExcation = ArrayList<String>()

    fun errorDialog(
        title:String,
        code:Int,
        mySharedPreferences: MySharedPreference?=null,
        onClick:(bool:Boolean)->Unit
    ){
        listExcation.add(title)
        dialogHelper.errorDialog(listExcation,code,mySharedPreferences){
            onClick.invoke(true)
        }
    }




    fun createSettings(){
        try {
            val myAppSettings = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + mContext.packageName)
            )
            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
            myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mActivity.startActivity(myAppSettings)
        }catch (e:Exception){
            errorDialog(e.message.toString(),-1){}
        }
    }


    fun callAdmin(phoneNumber:String){
        var intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:${phoneNumber}")
        mActivity.startActivity(intent)
    }


    fun inputTextCreateKeyboard(editText:EditText){
        try {
            editText.requestFocus()
            if (editText.requestFocus()){
                val inputMethodManager = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, AppConstant.ZERO)
            }
        }catch (e:Exception){
            errorDialog(e.message.toString(),-1){}
        }
    }

    fun hideKeyboard(editText:EditText){
        try {
            val imm = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(editText.windowToken, 0)
        }catch (e:Exception){
         errorDialog(e.message.toString(),-1){}
        }
    }


    val alertDialog = AlertDialog.Builder(activity)
    val create = alertDialog.create()
    fun loadingView(isLoading:Boolean){
      try {
          if (isLoading){
              val inflate = LoadingViewBinding.inflate(activity.layoutInflater)
              create.setView(inflate.root)
              create.show()
          }else{
              create.dismiss()
          }
      }catch (e:Exception){
          e.printStackTrace()
      }
        create.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        create.setCancelable(false)
    }





    fun colorStatusAndNavigationBars(color:Int){
        mActivity.window?.statusBarColor = color
        mActivity.window?.navigationBarColor = color
    }




    fun getNoData():String{
        return activity.getString(R.string.no_data)
    }

}


