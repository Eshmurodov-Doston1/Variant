package uz.gxteam.variant.ui.mainView.view.statement

import android.content.Context
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.gxteam.variant.ListenerActivity
import uz.gxteam.variant.R
import uz.gxteam.variant.adapters.stateMentAdapter.StatementAdapter
import uz.gxteam.variant.databinding.FragmentStateMentBinding
import uz.gxteam.variant.errors.errorInternet.errorNoClient
import uz.gxteam.variant.errors.errorInternet.noInternet
import uz.gxteam.variant.models.getApplications.DataApplication
import uz.gxteam.variant.resourse.stateMentApplications.ApplicationsResourse
import uz.gxteam.variant.ui.baseFragment.BaseFragment
import uz.gxteam.variant.vm.statementVm.StatementVm

@AndroidEntryPoint
class StateMentFragment : BaseFragment(R.layout.fragment_state_ment) {

    private val statementVm: StatementVm by viewModels()
    private val  binding:FragmentStateMentBinding by viewBinding()
    lateinit var stateMentAdapter:StatementAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            collapsing.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            collapsing.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

            stateMentAdapter = StatementAdapter(requireContext(),object:StatementAdapter.OnItemClickListener{
                override fun onItemClick(dataApplication: DataApplication, position: Int) {
                    var bundle = Bundle()
                    bundle.putSerializable("dataApplication",dataApplication)
                    findNavController().navigate(R.id.generateFragment,bundle)
                }
            })
        }
    }


    override fun onResume() {
        super.onResume()
        binding.apply {
            launch(Dispatchers.Main) {
                statementVm.getAllApplications().collect{
                    when(it){
                        is ApplicationsResourse.Loading->{
                            spinKit.visibility = View.VISIBLE
                        }
                        is ApplicationsResourse.SuccessApplications->{
                            spinKit.visibility = View.GONE

                            if (it.applications?.data?.isEmpty() == true){
                                animationView.visibility = View.VISIBLE
                                rvStatement.visibility  =View.GONE
                            }else{
                                animationView.visibility = View.GONE
                                rvStatement.visibility  =View.VISIBLE
                                stateMentAdapter.submitList(it.applications?.data)
                            }
                            rvStatement.adapter = stateMentAdapter
                        }
                        is ApplicationsResourse.ErrorApplications->{
                            spinKit.visibility = View.GONE
                            if (it.internetConnection==true){
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

}