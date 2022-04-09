package uz.gxteam.variant.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.gxteam.variant.ListenerActivity
import uz.gxteam.variant.R
import uz.gxteam.variant.databinding.FragmentAuthBinding
import uz.gxteam.variant.errors.authError.AuthErrors
import uz.gxteam.variant.errors.errorInternet.authError
import uz.gxteam.variant.errors.errorInternet.noInternet
import uz.gxteam.variant.models.auth.reqAuth.ReqAuth
import uz.gxteam.variant.resourse.authResourse.AuthResourse
import uz.gxteam.variant.vm.authViewModel.AuthViewModel
import kotlin.coroutines.CoroutineContext


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AuthFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth),CoroutineScope {
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

    private val binding:FragmentAuthBinding by viewBinding()
    private val authViewModel:AuthViewModel by viewModels()
    lateinit var listenerActivity:ListenerActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            if (!authViewModel.getSharedPreference().accessToken.equals("") && authViewModel.getSharedPreference().accessToken!=null ){
                listenerActivity.showLoading()
               findNavController().navigate(R.id.action_authFragment_to_lockFragment)
            }
            phoneNumber.requestFocus()

            phoneNumber.doAfterTextChanged {
                if (it.toString().trim().length==9){
                    password.requestFocus()
                }
            }



            var gson = Gson()
            login.setOnClickListener {
                val phoneNumber = "998${phoneNumber.text.toString().trim()}"
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

    fun Activity.changeStatusBarColor(color: Int, isLight: Boolean) {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = isLight
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
         * @return A new instance of fragment AuthFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AuthFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}