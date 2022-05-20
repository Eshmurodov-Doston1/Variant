package uz.gxteam.variant

import android.os.Build
import android.os.Bundle
import android.view.View
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import uz.gxteam.variant.databinding.ActivityMainBinding
import uz.gxteam.variant.vm.authViewModel.AuthViewModel
import uz.gxteam.variant.workManager.NotificationWork
import kotlin.coroutines.CoroutineContext


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),ListenerActivity,CoroutineScope {
    private val binding: ActivityMainBinding by viewBinding()
    private val authViewModel:AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(R.color.background)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        systemUI()
        var request = OneTimeWorkRequestBuilder<NotificationWork>().build()
        WorkManager.getInstance(this).enqueue(request)
    }





    override fun onNavigateUp(): Boolean {
        return findNavController(R.id.fragment).navigateUp()
    }

    override fun showLoading() {
        binding.include.loadingView.visibility  = View.VISIBLE
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun hideLoading() {
        binding.include.loadingView.visibility  = View.GONE
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

}