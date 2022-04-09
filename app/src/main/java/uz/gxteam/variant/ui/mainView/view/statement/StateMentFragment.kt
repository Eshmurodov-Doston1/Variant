package uz.gxteam.variant.ui.mainView.view.statement

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
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
import uz.gxteam.variant.vm.authViewModel.AuthViewModel
import uz.gxteam.variant.vm.statementVm.StatementVm
import kotlin.coroutines.CoroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StateMentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class StateMentFragment : Fragment(R.layout.fragment_state_ment),CoroutineScope {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private val statementVm: StatementVm by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val  binding:FragmentStateMentBinding by viewBinding()
    lateinit var stateMentAdapter:StatementAdapter
    lateinit var listenerActivity:ListenerActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            collapsing.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
            collapsing.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

            Log.e("Data", authViewModel.getSharedPreference().userData.toString())

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
            launch {
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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StateMentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StateMentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listenerActivity = activity as ListenerActivity
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}