package uz.gxteam.variant.ui.mainView.view.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.gxteam.variant.R
import uz.gxteam.variant.databinding.FragmentSettingsBinding
import uz.gxteam.variant.databinding.LogOutBinding
import uz.gxteam.variant.errors.errorInternet.errorNoClient
import uz.gxteam.variant.errors.errorInternet.noInternet
import uz.gxteam.variant.resourse.logOutResourse.LogOutResourse
import uz.gxteam.variant.ui.baseFragment.BaseFragment
import uz.gxteam.variant.utils.AppConstant.UNAUTHCODE
import uz.gxteam.variant.utils.AppConstant.ZERO
import uz.gxteam.variant.vm.authViewModel.AuthViewModel

@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.fragment_settings) {
    private val authViewModel:AuthViewModel by viewModels()
    private val binding:FragmentSettingsBinding by viewBinding()
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
                var dialog = AlertDialog.Builder(requireContext(), R.style.BottomSheetDialogThem)
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
                                        if (it.errorCode==UNAUTHCODE){
                                            var navOpitions = NavOptions.Builder().setPopUpTo(R.id.authFragment,false)
                                                .build()
                                            var bundle = Bundle()
                                            findNavController().navigate(R.id.authFragment,bundle,navOpitions)
                                        }else{
                                            errorNoClient(requireContext(),it.errorCode?:ZERO)
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
}