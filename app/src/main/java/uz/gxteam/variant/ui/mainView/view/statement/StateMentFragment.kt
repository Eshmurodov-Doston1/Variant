package uz.gxteam.variant.ui.mainView.view.statement

import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import uz.gxteam.variant.R
import uz.gxteam.variant.adapters.stateMentAdapter.StatementAdapter
import uz.gxteam.variant.databinding.FragmentStateMentBinding
import uz.gxteam.variant.errors.errorInternet.errorNoClient
import uz.gxteam.variant.errors.errorInternet.noInternet
import uz.gxteam.variant.models.getApplications.DataApplication
import uz.gxteam.variant.resourse.broadCastAuth.BroadCastAuthResourse
import uz.gxteam.variant.resourse.stateMentApplications.ApplicationsResourse
import uz.gxteam.variant.socket.SendSocketData
import uz.gxteam.variant.socket.dataSocket.DataSocket
import uz.gxteam.variant.ui.baseFragment.BaseFragment
import uz.gxteam.variant.utils.AppConstant
import uz.gxteam.variant.utils.AppConstant.AUTH_WST
import uz.gxteam.variant.utils.AppConstant.CHAT_APP_STATUS
import uz.gxteam.variant.utils.AppConstant.DATAAPPLICATION
import uz.gxteam.variant.utils.AppConstant.NEW_APPLICATION
import uz.gxteam.variant.utils.AppConstant.PUSHER_WST
import uz.gxteam.variant.utils.AppConstant.SUBSCRIBE_WST
import uz.gxteam.variant.utils.AppConstant.UNAUTHCODE
import uz.gxteam.variant.utils.AppConstant.WEBSOCKET_URL
import uz.gxteam.variant.utils.AppConstant.WST_CHANNEL
import uz.gxteam.variant.utils.AppConstant.WST_DATA
import uz.gxteam.variant.utils.AppConstant.WST_EVENT
import uz.gxteam.variant.utils.AppConstant.ZERO
import uz.gxteam.variant.vm.statementVm.StatementVm

@AndroidEntryPoint
class StateMentFragment : BaseFragment(R.layout.fragment_state_ment) {

    private val statementVm: StatementVm by viewModels()
    private val  binding:FragmentStateMentBinding by viewBinding()
    lateinit var stateMentAdapter:StatementAdapter
    lateinit var webSocketApp: WebSocket
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            collapsing.setExpandedTitleTextAppearance(R.style.ExpandedAppBar)
            collapsing.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar)

            stateMentAdapter = StatementAdapter(requireContext(),object:StatementAdapter.OnItemClickListener{
                override fun onItemClick(dataApplication: DataApplication, position: Int) {
                    var bundle = Bundle()
                    bundle.putSerializable(DATAAPPLICATION,dataApplication)
                    findNavController().navigate(R.id.generateFragment,bundle)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        socket()
        loadData()
    }

   fun loadData() {
       binding.apply {
           launch {
               statementVm.getAllApplications().collect {
                   when (it) {
                       is ApplicationsResourse.Loading -> {
                           spinKit.visibility = View.VISIBLE
                       }
                       is ApplicationsResourse.SuccessApplications -> {
                           spinKit.visibility = View.GONE

                           if (it.applications?.data?.isEmpty() == true) {
                               animationView.visibility = View.VISIBLE
                               rvStatement.visibility = View.GONE
                           } else {
                               animationView.visibility = View.GONE
                               rvStatement.visibility = View.VISIBLE
                               stateMentAdapter.submitList(it.applications?.data)
                           }
                           rvStatement.adapter = stateMentAdapter
                           stateMentAdapter.notifyDataSetChanged()
                       }
                       is ApplicationsResourse.ErrorApplications -> {
                           spinKit.visibility = View.GONE
                           if (it.internetConnection == true) {
                               if (it.errorCode == UNAUTHCODE) {
                                   var navOpitions =
                                       NavOptions.Builder().setPopUpTo(R.id.authFragment, false)
                                           .build()
                                   var bundle = Bundle()
                                   findNavController().navigate(
                                       R.id.authFragment,
                                       bundle,
                                       navOpitions
                                   )
                               } else {
                                   errorNoClient(requireContext(), it.errorCode ?: ZERO)
                               }
                           } else {
                               noInternet(requireContext())
                           }
                       }
                   }
               }
           }
       }

   }
    fun socket(){
        var gson = Gson()
        var client = OkHttpClient()
        val userData = gson.fromJson(statementVm.getShared().userData, uz.gxteam.variant.models.userData.UserData::class.java)
        try{
            val request: okhttp3.Request = okhttp3.Request.Builder().url(WEBSOCKET_URL).build()
            var listener = object:WebSocketListener(){
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                }
                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    super.onMessage(webSocket, bytes)

                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    val j = gson.fromJson(text, JsonObject::class.java)

                    if (j.has(WST_EVENT) && j.has(WST_DATA)){
                        if (j.has(WST_CHANNEL)){
                            if(j.has(WST_DATA)){
                                loadData()
                            }
                        }else
                        {
                            val socketData = gson.fromJson(j.get(WST_DATA).asString, DataSocket::class.java)
                            launch {
                                statementVm.broadCastAuth(SendSocketData("${NEW_APPLICATION}.${userData.id}",socketData.socket_id))
                                    .collect{
                                        when(it){
                                            is BroadCastAuthResourse.SuccessBroadCast->{
                                                webSocket.send(" {\"${WST_EVENT}\":\"${PUSHER_WST}:${SUBSCRIBE_WST}\",\"${WST_DATA}\":{\"${AUTH_WST}\":\"${it.resSocket?.auth}\",\"${WST_CHANNEL}\":\"${NEW_APPLICATION}.${userData.id}\"}}")
                                            }
                                            is BroadCastAuthResourse.ErrorBroadCast->{
                                                if (it.errorCode==UNAUTHCODE){
                                                    var navOpitions = NavOptions.Builder().setPopUpTo(R.id.authFragment,false).build()
                                                    var bundle = Bundle()
                                                    findNavController().navigate(R.id.authFragment,bundle,navOpitions)
                                                }else{
                                                    errorNoClient(requireContext(),it.errorCode?:ZERO)
                                                }
                                            }
                                        }
                                    }
                            }

                            launch {
                                statementVm.broadCastAuth(SendSocketData(CHAT_APP_STATUS,socketData.socket_id))
                                    .collect{
                                        when(it){
                                            is BroadCastAuthResourse.SuccessBroadCast->{
                                                webSocket.send(" {\"${WST_EVENT}\":\"${PUSHER_WST}:${SUBSCRIBE_WST}\",\"${WST_DATA}\":{\"${AUTH_WST}\":\"${it.resSocket?.auth}\",\"${WST_CHANNEL}\":\"${CHAT_APP_STATUS}\"}}")
                                            }
                                            is BroadCastAuthResourse.ErrorBroadCast->{
                                                if (it.errorCode==UNAUTHCODE){
                                                    var navOpitions = NavOptions.Builder().setPopUpTo(R.id.authFragment,false).build()
                                                    var bundle = Bundle()
                                                    findNavController().navigate(R.id.authFragment,bundle,navOpitions)
                                                }else{
                                                    errorNoClient(requireContext(),it.errorCode?:ZERO)
                                                }
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }
                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                }
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    webSocket.close(code,reason)
                }
            }
            webSocketApp = client.newWebSocket(request,listener)
            client.dispatcher.executorService.shutdown()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}