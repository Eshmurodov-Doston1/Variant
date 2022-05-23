package uz.gxteam.variant.broadCast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import uz.gxteam.variant.service.MyForegroundService

class ApplicationBroadCast:BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action){
            var service = Intent(context,MyForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service)
            }else{
                context.startService(service)
            }
        }
    }
}