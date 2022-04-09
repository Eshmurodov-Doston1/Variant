package uz.gxteam.variant

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.viewbinding.library.activity.viewBinding
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.gson.Gson

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import uz.gxteam.variant.databinding.ActivityMainBinding
import uz.gxteam.variant.databinding.FullScreenBinding
import uz.gxteam.variant.errors.errorInternet.errorNoClient
import uz.gxteam.variant.errors.errorInternet.noInternet
import uz.gxteam.variant.models.getApplications.DataApplication
import uz.gxteam.variant.resourse.userResourse.UserDataResourse
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

        var request = OneTimeWorkRequestBuilder<NotificationWork>().build()
        WorkManager.getInstance(this).enqueue(request)




        if (authViewModel.getSharedPreference().theme==true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE
            window.statusBarColor = Color.parseColor("#0D1A28")
            window.navigationBarColor = Color.parseColor("#0D1A28")
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.parseColor("#FFFFFF")
            window.navigationBarColor = Color.parseColor("#FFFFFF")
        }




    }





    override fun onNavigateUp(): Boolean {
        return findNavController(R.id.fragment).navigateUp()
    }

    override fun showLoading() {
        binding.include.loadingView.visibility  = View.VISIBLE
   //     window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun hideLoading() {
        binding.include.loadingView.visibility  = View.GONE
     //   window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
    }

    override fun createStateMentView(dataApplication: DataApplication) {
//        var bundle = Bundle()
//        bundle.putSerializable("dataApplication",dataApplication)
//        findNavController(R.id.fragment).navigate(R.id.createStatementFragment,bundle)
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
}