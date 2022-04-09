package uz.gxteam.variant.ui.chatView

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okio.ByteString
import org.java_websocket.WebSocketListener
import uz.gxteam.variant.R
import uz.gxteam.variant.adapters.chatListAdapter.chat.ChatAdapter
import uz.gxteam.variant.databinding.FragmentChatBinding
import uz.gxteam.variant.errors.errorInternet.errorNoClient
import uz.gxteam.variant.errors.errorInternet.noInternet
import uz.gxteam.variant.models.getApplications.DataApplication
import uz.gxteam.variant.models.messages.reqMessage.ReqMessage
import uz.gxteam.variant.models.messages.resMessage.Message
import uz.gxteam.variant.models.sendMessage.sendMessage.SendMessageUser
import uz.gxteam.variant.models.userData.UserData
import uz.gxteam.variant.resourse.broadCastAuth.BroadCastAuthResourse
import uz.gxteam.variant.resourse.message.AllMessageResourse
import uz.gxteam.variant.resourse.messageResourse.MessageResourse
import uz.gxteam.variant.socket.SendSocketData
import uz.gxteam.variant.socket.connectSocket.ConnectSocket
import uz.gxteam.variant.socket.dataSocket.DataSocket
import uz.gxteam.variant.socket.socketMessage.SocketMessage
import uz.gxteam.variant.vm.authViewModel.AuthViewModel
import uz.gxteam.variant.vm.statementVm.StatementVm
import uz.gxteam.variant.workManager.NotificationWork
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "dataApplication"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat),CoroutineScope {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var param3: DataApplication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param3 = it.getSerializable(ARG_PARAM3) as DataApplication
        }
    }

    private val binding:FragmentChatBinding by viewBinding()
    private val stamentVm:StatementVm by viewModels()
    private val authViewModel:AuthViewModel by viewModels()
    lateinit var chatAdapter:ChatAdapter
    lateinit var listMessage:ArrayList<Message>
    lateinit var gson:Gson
    var count = 0
    var webSocketApp: WebSocket? = null
    var client: OkHttpClient? = null
    var userId:Int=-1
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
                    launch {
                        stamentVm.sendMessage(SendMessageUser(message, param3?.token.toString()))
                            .collect{
                                when(it){
                                    is MessageResourse.SuccessMessage->{
                                        text.text.clear()
//                                        chatAdapter.notifyItemInserted(listMessage.size)
//                                        rvChat.smoothScrollToPosition(listMessage.size)

                                    }
                                    is MessageResourse.ErrorMessage->{
                                        if(it.internetConnection==true){
                                            if (it.errorCode==401){
                                                var navOpitions = NavOptions.Builder().setPopUpTo(R.id.authFragment,false)
                                                    .build()
                                                var bundle = Bundle()
                                                findNavController().navigate(R.id.authFragment,bundle,navOpitions)
                                            }else{
                                                errorNoClient(requireContext(),it.errorCode?:0)
                                            }
                                        }else{
                                            noInternet(requireContext())
                                        }
                                    }
                                }
                            }
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
            launch {
                stamentVm.getAllMessage(ReqMessage(param3?.token.toString()))
                    .collect{
                    when(it){
                        is AllMessageResourse.Loading->{
                            spinKit.visibility = View.VISIBLE
                        }
                        is AllMessageResourse.SuccessAllMessage->{
                            chatAdapter = ChatAdapter(userId)
                            listMessage = ArrayList()
                            listMessage.addAll(it.resMessage?.messages?: emptyList())
                            spinKit.visibility = View.GONE
                            chatAdapter.submitList(listMessage)
                            rvChat.adapter = chatAdapter
                        }
                        is AllMessageResourse.ErrorAllMessage->{
                            spinKit.visibility = View.GONE
                            if (it.internetConnection==true){
                                if (it.errorCode==401){
                                    var navOpitions = NavOptions.Builder().setPopUpTo(R.id.authFragment,false)
                                        .build()
                                    var bundle = Bundle()
                                    findNavController().navigate(R.id.authFragment,bundle,navOpitions)
                                    authViewModel.getSharedPreference().clear()
                                }else{
                                    errorNoClient(requireContext(),it.errorCode?:0)
                                }
                            }else{
                                noInternet(requireContext())
                            }
                        }
                    }
                }
            }
        }
    }




    fun socketData(){
        try {
            val request: okhttp3.Request = okhttp3.Request.Builder().url("ws://web.variantgroup.uz:6001/app/mykey?protocol=7&client=js&version=7.0.6&flash=false").build()
            var listener = object:okhttp3.WebSocketListener(){
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    val socketData = gson.fromJson(text, ConnectSocket::class.java)
                    if (count==0){
                        val dataSocket = gson.fromJson(socketData.data, DataSocket::class.java)
                        launch {
                            stamentVm.broadCastAuth(SendSocketData("private-ChatNewMessage.${param3?.token}", dataSocket.socket_id))
                                .collect{
                                    when(it){
                                        is BroadCastAuthResourse.SuccessBroadCast->{
                                            webSocket.send(" {\"event\":\"pusher:subscribe\",\"data\":{\"auth\":\"${it.resSocket?.auth}\",\"channel\":\"private-ChatNewMessage.${param3?.token}\"}}")
                                            count++
                                        }
                                        is BroadCastAuthResourse.ErrorBroadCast->{
                                            if (it.internetConnection==true){
                                                if (it.errorCode==401){
                                                    var navOpitions = NavOptions.Builder().setPopUpTo(R.id.authFragment,false).build()
                                                    var bundle = Bundle()
                                                    findNavController().navigate(R.id.authFragment,bundle,navOpitions)
                                                }else{
                                                    errorNoClient(requireContext(),it.errorCode?:0)
                                                }
                                            }else{
                                                noInternet(requireContext())
                                            }
                                        }
                                    }
                                }
                        }
                    }else{
                        if (socketData.data!=null){
                            val messageSocket = gson.fromJson(socketData.data, SocketMessage::class.java)
                            Log.e("Messages", messageSocket.toString())
                            GlobalScope.launch(Dispatchers.Main) {
                                if (messageSocket.type==1){
                                    listMessage.add(Message(messageSocket.app_id,listMessage.size+1,messageSocket.message,null,messageSocket.app_id,messageSocket.type,-2))
                                }else{
                                    listMessage.add(Message(messageSocket.app_id,listMessage.size+1,messageSocket.message,null,messageSocket.app_id,messageSocket.type,userId))
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
       webSocketApp?.close(1000,"Close Socket")
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}