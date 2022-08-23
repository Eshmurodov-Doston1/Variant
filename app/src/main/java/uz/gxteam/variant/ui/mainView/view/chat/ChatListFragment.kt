package uz.gxteam.variant.ui.mainView.view.chat

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.gxteam.variant.R
import uz.gxteam.variant.adapters.chatListAdapter.ChatListAdapter
import uz.gxteam.variant.databinding.FragmentChatListBinding
import uz.gxteam.variant.models.getApplications.DataApplication
import uz.gxteam.variant.ui.baseFragment.BaseFragment
import uz.gxteam.variant.utils.AppConstant.DATAAPPLICATION
import uz.gxteam.variant.utils.fetchResult
import uz.gxteam.variant.vm.statementVm.StatementVm

@AndroidEntryPoint
class ChatListFragment : BaseFragment(R.layout.fragment_chat_list) {

    private val binding:FragmentChatListBinding by viewBinding()
    private val statementVM:StatementVm by viewModels()
    lateinit var chatListAdapter:ChatListAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            collapsing.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            collapsing.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
            chatListAdapter = ChatListAdapter(requireContext(),object:ChatListAdapter.OnItemClickListener{
                override fun onItemClick(dataApplication: DataApplication, position: Int) {
                    var bundle = Bundle()
                    bundle.putSerializable(DATAAPPLICATION,dataApplication)
                    findNavController().navigate(R.id.chatFragment,bundle)
                }
            })
        }
    }


    override fun onResume() {
        super.onResume()
        binding.apply {
            statementVM.getAllApplications()
            launch {
                statementVM.getAllApplications.fetchResult(compositionRoot.uiControllerApp,{ result->
                    if (result?.data?.isEmpty() == true){
                        animationView.visibility = View.VISIBLE
                        rvStatement.visibility  =View.GONE
                    }else{
                        animationView.visibility = View.GONE
                        rvStatement.visibility  =View.VISIBLE
                        chatListAdapter.submitList(result?.data)
                    }
                    rvStatement.adapter = chatListAdapter
                },{isClick ->  })
            }
        }
    }
}