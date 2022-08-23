package uz.gxteam.variant.ui.chatView

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import uz.gxteam.variant.R
import uz.gxteam.variant.adapters.chatListAdapter.chat.ChatAdapter
import uz.gxteam.variant.databinding.FragmentChatBinding
import uz.gxteam.variant.models.getApplications.DataApplication
import uz.gxteam.variant.models.messages.reqMessage.ReqMessage
import uz.gxteam.variant.models.messages.resMessage.Message
import uz.gxteam.variant.models.sendMessage.sendMessage.SendMessageUser
import uz.gxteam.variant.models.userData.UserData
import uz.gxteam.variant.socket.SendSocketData
import uz.gxteam.variant.socket.connectSocket.ConnectSocket
import uz.gxteam.variant.socket.dataSocket.DataSocket
import uz.gxteam.variant.socket.socketMessage.SocketMessage
import uz.gxteam.variant.ui.baseFragment.BaseFragment
import uz.gxteam.variant.utils.AppConstant.AUTH_WST
import uz.gxteam.variant.utils.AppConstant.CHAT_MEW_MESSAGE
import uz.gxteam.variant.utils.AppConstant.CLOSE_WST_TEXT
import uz.gxteam.variant.utils.AppConstant.DATAAPPLICATION
import uz.gxteam.variant.utils.AppConstant.MINUS_ONE
import uz.gxteam.variant.utils.AppConstant.MINUS_TWO
import uz.gxteam.variant.utils.AppConstant.ONE
import uz.gxteam.variant.utils.AppConstant.PUSHER_WST
import uz.gxteam.variant.utils.AppConstant.SUBSCRIBE_WST
import uz.gxteam.variant.utils.AppConstant.WEBSOCKET_URL
import uz.gxteam.variant.utils.AppConstant.WST_CHANNEL
import uz.gxteam.variant.utils.AppConstant.WST_DATA
import uz.gxteam.variant.utils.AppConstant.WST_EVENT
import uz.gxteam.variant.utils.AppConstant.ZERO
import uz.gxteam.variant.utils.fetchResult
import uz.gxteam.variant.vm.authViewModel.AuthViewModel
import uz.gxteam.variant.vm.statementVm.StatementVm
import uz.gxteam.variant.workManager.NotificationWork

@AndroidEntryPoint
class ChatFragment : BaseFragment(R.layout.fragment_chat) {
    private var param3: DataApplication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param3 = it.getSerializable(DATAAPPLICATION) as DataApplication
        }
    }

    private val binding:FragmentChatBinding by viewBinding()
    private val stamentVm:StatementVm by viewModels()
    private val authViewModel:AuthViewModel by viewModels()
    lateinit var chatAdapter:ChatAdapter
    lateinit var listMessage:ArrayList<Message>
    lateinit var gson:Gson
    var count = ZERO
    var webSocketApp: WebSocket? = null
    var client: OkHttpClient? = null
    var userId:Int= MINUS_ONE
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            gson = Gson()
            listMessage = ArrayList()
            loadData()
            authViewModel.getSharedPreference().oldToken = param3?.token
            var request = OneTimeWorkRequestBuilder<NotificationWork>().build()
            WorkManager.getInstance(requireContext())
                .enqueue(request)
            back.setOnClickListener {
                findNavController().popBackStack()
            }
            sendText.setOnClickListener {
                 val message = text.text.toString().trim()
                if (message.isNotBlank() && message.isNotEmpty()){
                    stamentVm.sendMessage(SendMessageUser(message, param3?.token.toString()))
                    launch {
                        stamentVm.sendMessage.fetchResult(compositionRoot.uiControllerApp,{ result->
                            text.text.clear()
                        },{ isClick ->

                        })
                    }
                }
            }

            client = OkHttpClient()
            socketData()
        }
    }
    fun loadData(){
        var userData = gson.fromJson(authViewModel.getSharedPreference().userData,UserData::class.java)
        userId = userData.id
        binding.apply {
            textUser.text = param3?.full_name
            stamentVm.getAllMessage(ReqMessage(param3?.token.toString()))
            launch {
              stamentVm.getAllMessage.fetchResult(compositionRoot.uiControllerApp,{ result->
                  chatAdapter = ChatAdapter(userId)
                  listMessage = ArrayList()
                  listMessage.addAll(result?.messages?: emptyList())
                  spinKit.visibility = View.GONE
                  chatAdapter.submitList(listMessage)
                  rvChat.adapter = chatAdapter
              },{isClick ->  })
            }
        }
    }
    fun socketData(){
        try {
            val request: okhttp3.Request = okhttp3.Request.Builder().url(WEBSOCKET_URL).build()
            var listener = object:okhttp3.WebSocketListener(){
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    val socketData = gson.fromJson(text, ConnectSocket::class.java)
                    if (count==ZERO){
                        val dataSocket = gson.fromJson(socketData.data, DataSocket::class.java)
                        stamentVm.broadCastAuth(SendSocketData("${CHAT_MEW_MESSAGE}.${param3?.token}", dataSocket.socket_id))
                        launch {
                           stamentVm.broadCastAuth.fetchResult(compositionRoot.uiControllerApp,{ result->
                               webSocket.send(" {\"${WST_EVENT}\":\"${PUSHER_WST}:${SUBSCRIBE_WST}\",\"${WST_DATA}\":{\"${AUTH_WST}\":\"${result?.auth}\",\"${WST_CHANNEL}\":\"${CHAT_MEW_MESSAGE}.${param3?.token}\"}}")
                               count++
                           },{isClick ->  })
                        }
                    }else{
                        if (socketData.data!=null){
                            val messageSocket = gson.fromJson(socketData.data, SocketMessage::class.java)
                            GlobalScope.launch(Dispatchers.Main) {
                                if (messageSocket.type== ONE){
                                    listMessage.add(Message(messageSocket.app_id,listMessage.size+ ONE,messageSocket.message,null,messageSocket.app_id,messageSocket.type, MINUS_TWO))
                                }else{
                                    listMessage.add(Message(messageSocket.app_id,listMessage.size+ ONE,messageSocket.message,null,messageSocket.app_id,messageSocket.type,userId))
                                }
                                chatAdapter.notifyItemInserted(listMessage.size)
                                binding.rvChat.smoothScrollToPosition(listMessage.size)
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
            webSocketApp = client!!.newWebSocket(request, listener)
            client!!.dispatcher.executorService.shutdown()

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
       webSocketApp?.close(1000,CLOSE_WST_TEXT)
    }
}