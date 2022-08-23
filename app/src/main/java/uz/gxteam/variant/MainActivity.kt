package uz.gxteam.variant

import android.app.ActivityManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import uz.gxteam.variant.databinding.ActivityMainBinding
import uz.gxteam.variant.service.MyForegroundService
import uz.gxteam.variant.utils.container.AppCompositionRoot
import uz.gxteam.variant.utils.uiController.UiController
import uz.gxteam.variant.vm.authViewModel.AuthViewModel
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),ListenerActivity,CoroutineScope,UiController {
    private val binding: ActivityMainBinding by viewBinding()
    private val authViewModel:AuthViewModel by viewModels()
    lateinit var appCompositionRoot: AppCompositionRoot
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.background)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        appCompositionRoot = AppCompositionRoot(this,navHostFragment.navController,this,authViewModel.mySharedPreferenceApp)
        systemUI()
    }

    fun foregroundServiceRunning():Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (MyForegroundService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }





    override fun onNavigateUp(): Boolean {
        return findNavController(R.id.fragment).navigateUp()
    }

    override fun uploadLoadingShow() {
        binding.includeUploadLoading.loadingView.visibility = View.VISIBLE
    }

    override fun uploadLoadingHide() {
        binding.includeUploadLoading.loadingView.visibility = View.INVISIBLE
    }


    override fun onBackPressed() {
        val findNavController = findNavController(R.id.fragment)
        if (findNavController.currentDestination?.id==R.id.authFragment){
            finish()
        }else if (findNavController.currentDestination?.id==R.id.lockFragment){
            finish()
        }else if (findNavController.currentDestination?.id==R.id.mainFragment){
            finish()
        }else{
            findNavController.popBackStack()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main


    private fun systemUI() {
        if (authViewModel.getSharedPreference().theme==true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE
            window.statusBarColor = resources.getColor(R.color.statusbar_color)
            window.navigationBarColor =resources.getColor(R.color.statusbar_color)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor =resources.getColor(R.color.statusbar_color)
            window.navigationBarColor = resources.getColor(R.color.statusbar_color)
        }
    }

    override fun showProgress() {
        appCompositionRoot.loadingView(true)
    }

    override fun hideProgress() {
        Log.e("HideProgressData", "hideProgress: ", )
        appCompositionRoot.loadingView(false)
    }

    override fun error(errorCode: Long, errorMessage: String,onClick:(isClick:Boolean)->Unit) {
        appCompositionRoot.errorDialog(errorMessage, errorCode.toInt(), authViewModel.mySharedPreferenceApp,onClick)
    }

}