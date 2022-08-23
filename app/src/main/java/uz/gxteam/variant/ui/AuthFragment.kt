package uz.gxteam.variant.ui

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uz.gxteam.variant.R
import uz.gxteam.variant.databinding.FragmentAuthBinding
import uz.gxteam.variant.models.auth.reqAuth.ReqAuth
import uz.gxteam.variant.ui.baseFragment.BaseFragment
import uz.gxteam.variant.utils.AppConstant.PHONE_UZB
import uz.gxteam.variant.utils.fetchResult
import uz.gxteam.variant.vm.authViewModel.AuthViewModel

@AndroidEntryPoint
class AuthFragment : BaseFragment(R.layout.fragment_auth),CoroutineScope {
    private val binding:FragmentAuthBinding by viewBinding()
    private val authViewModel:AuthViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            if (!authViewModel.getSharedPreference().accessToken.equals("") && authViewModel.getSharedPreference().accessToken!=null){
               findNavController().navigate(R.id.action_authFragment_to_lockFragment)
            }
            phoneNumber.requestFocus()

            phoneNumber.doAfterTextChanged {
                if (it.toString().trim().length==9){
                    password.requestFocus()
                }
            }

            login.setOnClickListener {
                val phoneNumber = "${PHONE_UZB}${phoneNumber.text.toString().trim()}"
                val password = password.text.toString().trim()
                if (phoneNumber.isEmpty()){
                    textInput.error = getString(R.string.no_phone)
                }else if (phoneNumber.length<9){
                    textInput.error = getString(R.string.error_phone)
                }else if (password.isEmpty()){
                    textInput1.error = getString(R.string.no_password)
                }else if (password.length<8){
                    textInput1.error = getString(R.string.password_length)
                } else if (phoneNumber.isNotEmpty() && password.isNotEmpty()){
                    authViewModel.authApp(ReqAuth(password,phoneNumber))
                    lifecycleScope.launch {
                            authViewModel.authVariant.fetchResult(compositionRoot.uiControllerApp,{ result->
                                compositionRoot.mySharedPreferencesApp.accessToken = result?.access_token
                                compositionRoot.mySharedPreferencesApp.refreshToken = result?.refresh_token
                                compositionRoot.mySharedPreferencesApp.tokenType = result?.token_type
                                findNavController().navigate(R.id.action_authFragment_to_lockFragment)
                            },{ isClick ->

                            })
                    }
                }
            }
        }
    }

}