package uz.gxteam.variant.ui.mainView.view.settings

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.gxteam.variant.ListenerActivity
import uz.gxteam.variant.R
import uz.gxteam.variant.databinding.FragmentSettingsBinding
import uz.gxteam.variant.databinding.LogOutBinding
import uz.gxteam.variant.errors.errorInternet.errorNoClient
import uz.gxteam.variant.errors.errorInternet.noInternet
import uz.gxteam.variant.resourse.logOutResourse.LogOutResourse
import uz.gxteam.variant.vm.authViewModel.AuthViewModel
import kotlin.coroutines.CoroutineContext
import kotlin.math.log

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings),CoroutineScope {
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
    private val authViewModel:AuthViewModel by viewModels()
   private val binding:FragmentSettingsBinding by viewBinding()
    lateinit var listenerActivity: ListenerActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            switchButton.isChecked = authViewModel.getSharedPreference().theme == true
            switchButton.setOnCheckedChangeListener{ view, isChecked ->
                if (isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    authViewModel.getSharedPreference().theme = true
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    authViewModel.getSharedPreference().theme = false
                }
            }

            logout.setOnClickListener {
                var dialog = AlertDialog.Builder(requireContext(),R.style.BottomSheetDialogThem)
                val create = dialog.create()
                var logOutBinding = LogOutBinding.inflate(LayoutInflater.from(requireContext()),null,false)
                create.setView(logOutBinding.root)
                logOutBinding.noBtn.setOnClickListener {
                    create.dismiss()
                }

                logOutBinding.okBtn.setOnClickListener {
                    launch {
                        authViewModel.logOut().collect{
                            when(it){
                                is LogOutResourse.Loading->{
                                    listenerActivity.showLoading()
                                }
                                is LogOutResourse.SuccessLogOut->{
                                    listenerActivity.hideLoading()
                                    authViewModel.getSharedPreference().clear()
                                    var navOpitions = NavOptions.Builder().setPopUpTo(R.id.authFragment,false).build()
                                    var bundle = Bundle()
                                    findNavController().navigate(R.id.authFragment,bundle,navOpitions)
                                    create.dismiss()
                                }
                                is LogOutResourse.ErrorLogOut->{
                                    listenerActivity.hideLoading()
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
                                    create.dismiss()
                                }
                            }
                        }
                    }
                }
                create.show()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listenerActivity = activity as ListenerActivity
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}