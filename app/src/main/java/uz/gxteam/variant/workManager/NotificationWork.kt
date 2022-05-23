package uz.gxteam.variant.workManager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import uz.gxteam.variant.MainActivity
import uz.gxteam.variant.R
import uz.gxteam.variant.interceptor.MySharedPreference
import uz.gxteam.variant.repository.stateMent.StateMentRepository
import uz.gxteam.variant.socket.SendSocketData
import uz.gxteam.variant.socket.connectSocket.ConnectSocket
import uz.gxteam.variant.socket.dataSocket.DataSocket
import uz.gxteam.variant.socket.socketMessage.SocketMessage
import uz.gxteam.variant.utils.AppConstant.AUTH_WST
import uz.gxteam.variant.utils.AppConstant.CHAT_MEW_MESSAGE
import uz.gxteam.variant.utils.AppConstant.COMPANYNAME
import uz.gxteam.variant.utils.AppConstant.EMPTYTEXT
import uz.gxteam.variant.utils.AppConstant.PUSHER_WST
import uz.gxteam.variant.utils.AppConstant.SUBSCRIBE_WST
import uz.gxteam.variant.utils.AppConstant.WEBSOCKET_URL
import uz.gxteam.variant.utils.AppConstant.WST_CHANNEL
import uz.gxteam.variant.utils.AppConstant.WST_DATA
import uz.gxteam.variant.utils.AppConstant.WST_EVENT


@HiltWorker
class NotificationWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val stateMentRepository:StateMentRepository,
    private val mySharedPreference: MySharedPreference
    ):Worker(appContext,workerParams) {
    var CHANNEL_ID="1"
    lateinit var webSocketApp:WebSocket
    override fun doWork(): Result {
       // socketData()
        return Result.success()
    }

    fun socketData(){
        if (!mySharedPreference.accessToken.equals(EMPTYTEXT)){
            var count=0
            var gson = Gson()
            var client = OkHttpClient()
            try {
                val request: okhttp3.Request = okhttp3.Request.Builder().url(WEBSOCKET_URL).build()
                var listener = object:okhttp3.WebSocketListener(){
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        super.onOpen(webSocket, response)
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        super.onMessage(webSocket, text)
                        val socketData = gson.fromJson(text, ConnectSocket::class.java)
                        if (count==0){
                            val dataSocket = gson.fromJson(socketData.data, DataSocket::class.java)
                            GlobalScope.launch(Dispatchers.IO){
                                stateMentRepository.broadCastingAuth(SendSocketData("${CHAT_MEW_MESSAGE}.${mySharedPreference.oldToken}", dataSocket.socket_id),"${mySharedPreference.tokenType} ${mySharedPreference.accessToken}")
                                    .collect{
                                        if (it.isSuccessful){
                                            webSocket.send(" {\"${WST_EVENT}\":\"${PUSHER_WST}:${SUBSCRIBE_WST}\",\"${WST_DATA}\":{\"${AUTH_WST}\":\"${it.body()?.auth}\",\"${WST_CHANNEL}\":\"${CHAT_MEW_MESSAGE}.${mySharedPreference.oldToken}\"}}")
                                            count++
                                        }
                                    }
                            }
                        } else{
                            if (socketData.data!=null){
                                val messageSocket = gson.fromJson(socketData.data, SocketMessage::class.java)
                                GlobalScope.launch(Dispatchers.Main) {
                                    if (messageSocket.type==1){
                                        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                                        var resultIntent =  Intent(applicationContext, MainActivity::class.java)
                                        var resultPendingIntent = PendingIntent.getActivity(applicationContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                                        var notificationCompat =   NotificationCompat.Builder(applicationContext,CHANNEL_ID)
                                            .setSmallIcon(R.drawable.ic_baseline_message_24)
                                            .setContentTitle(applicationContext.getText(R.string.app_name))
                                            .setContentText(messageSocket.message)
                                            .setSound(alarmSound)
                                            .setContentIntent(resultPendingIntent)
                                            .setAutoCancel(true)
                                            .build()

                                        val notificationManager = applicationContext.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            val descriptionText = COMPANYNAME
                                            val importance = NotificationManager.IMPORTANCE_HIGH
                                            val channel = NotificationChannel(CHANNEL_ID, COMPANYNAME, importance).apply { description =
                                                descriptionText
                                            }
                                            notificationManager.createNotificationChannel(channel)
                                        }
                                        notificationManager.notify(1,notificationCompat)
                                    }
                                }
                            }
                            count++
                        }
                    }

                    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                        super.onMessage(webSocket, bytes)
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        super.onClosed(webSocket, code, reason)
                        webSocket.close(code,reason)
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        super.onFailure(webSocket, t, response)
                    }
                }
                webSocketApp = client.newWebSocket(request, listener)
                client.dispatcher.executorService.shutdown()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }




}