package uz.gxteam.variant.ui.mainView.view.statement

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import uz.gxteam.variant.R
import uz.gxteam.variant.adapters.stateMentAdapter.StatementAdapter
import uz.gxteam.variant.databinding.FragmentStateMentBinding
import uz.gxteam.variant.models.getApplications.DataApplication
import uz.gxteam.variant.socket.SendSocketData
import uz.gxteam.variant.socket.dataSocket.DataSocket
import uz.gxteam.variant.ui.baseFragment.BaseFragment
import uz.gxteam.variant.utils.AppConstant.AUTH_WST
import uz.gxteam.variant.utils.AppConstant.CHAT_APP_STATUS
import uz.gxteam.variant.utils.AppConstant.DATAAPPLICATION
import uz.gxteam.variant.utils.AppConstant.NEW_APPLICATION
import uz.gxteam.variant.utils.AppConstant.PUSHER_WST
import uz.gxteam.variant.utils.AppConstant.SUBSCRIBE_WST
import uz.gxteam.variant.utils.AppConstant.WEBSOCKET_URL
import uz.gxteam.variant.utils.AppConstant.WST_CHANNEL
import uz.gxteam.variant.utils.AppConstant.WST_DATA
import uz.gxteam.variant.utils.AppConstant.WST_EVENT
import uz.gxteam.variant.utils.fetchResult
import uz.gxteam.variant.vm.statementVm.StatementVm
import java.util.concurrent.TimeUnit


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
           statementVm.getAllApplications()
           launch {
               statementVm.getAllApplications.fetchResult(compositionRoot.uiControllerApp,{ result->
                   if (result?.data?.isEmpty() == true) {
                       animationView.visibility = View.VISIBLE
                       rvStatement.visibility = View.GONE
                   } else {
                       animationView.visibility = View.GONE
                       rvStatement.visibility = View.VISIBLE
                       stateMentAdapter.submitList(result?.data)
                   }
                   rvStatement.adapter = stateMentAdapter
                   stateMentAdapter.notifyDataSetChanged()
               },{isClick ->  })
           }
       }

   }
    fun socket(){
        var gson = Gson()
        var client: OkHttpClient

        val userData = gson.fromJson(statementVm.getShared().userData, uz.gxteam.variant.models.userData.UserData::class.java)
        try{



            client = OkHttpClient.Builder().readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .build()


            val request: okhttp3.Request = okhttp3.Request.Builder().url(WEBSOCKET_URL)
            .build()
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
                            statementVm.broadCastAuth(SendSocketData("${NEW_APPLICATION}.${userData.id}",socketData.socket_id))
                            launch {
                              statementVm.broadCastAuth.fetchResult(compositionRoot.uiControllerApp,{ result->
                                  webSocket.send(" {\"${WST_EVENT}\":\"${PUSHER_WST}:${SUBSCRIBE_WST}\",\"${WST_DATA}\":{\"${AUTH_WST}\":\"${result?.auth}\",\"${WST_CHANNEL}\":\"${NEW_APPLICATION}.${userData.id}\"}}")
                              },{isClick ->  })
                            }

                            statementVm.broadCastAuth(SendSocketData(CHAT_APP_STATUS,socketData.socket_id))
                            launch {
                                statementVm.broadCastAuth.fetchResult(compositionRoot.uiControllerApp,{ result->
                                    webSocket.send(" {\"${WST_EVENT}\":\"${PUSHER_WST}:${SUBSCRIBE_WST}\",\"${WST_DATA}\":{\"${AUTH_WST}\":\"${result?.auth}\",\"${WST_CHANNEL}\":\"${CHAT_APP_STATUS}\"}}")
                                },{isClick ->  })
                            }
                        }
                    }
                }
                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    webSocket.close(1000, null);
                }
            }
            webSocketApp = client.newWebSocket(request,listener)
            client.dispatcher.executorService.shutdown()

        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}