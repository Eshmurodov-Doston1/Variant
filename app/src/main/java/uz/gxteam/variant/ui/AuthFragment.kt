package uz.gxteam.variant.ui

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import uz.gxteam.variant.R
import uz.gxteam.variant.databinding.FragmentAuthBinding
import uz.gxteam.variant.errors.authError.AuthErrors
import uz.gxteam.variant.errors.errorInternet.authError
import uz.gxteam.variant.errors.errorInternet.noInternet
import uz.gxteam.variant.models.auth.reqAuth.ReqAuth
import uz.gxteam.variant.resourse.authResourse.AuthResourse
import uz.gxteam.variant.ui.baseFragment.BaseFragment
import uz.gxteam.variant.utils.AppConstant.PHONE_UZB
import uz.gxteam.variant.vm.authViewModel.AuthViewModel
@AndroidEntryPoint
class AuthFragment : BaseFragment(R.layout.fragment_auth),CoroutineScope {
    private val binding:FragmentAuthBinding by viewBinding()
    private val authViewModel:AuthViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            var gson = Gson()
            if (!authViewModel.getSharedPreference().accessToken.equals("") && authViewModel.getSharedPreference().accessToken!=null){
                listenerActivity.showLoading()
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
                    lifecycleScope.launch {
                        authViewModel.authApp(ReqAuth(password,phoneNumber)).collect{
                            when(it){
                                is AuthResourse.Loading->{
                                    listenerActivity.showLoading()
                                }
                                is AuthResourse.SuccessAuth->{
                                    listenerActivity.hideLoading()
                                    findNavController().navigate(R.id.action_authFragment_to_lockFragment)
                                }
                                is AuthResourse.ErrorAuth->{
                                    listenerActivity.hideLoading()
                                    if (it.internetConnection==true){
                                        val error = gson.fromJson(it.error, AuthErrors::class.java)
                                        authError(error,requireContext(),it.errorCode?:0)
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
    }

}