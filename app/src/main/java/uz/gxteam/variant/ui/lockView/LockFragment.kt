package uz.gxteam.variant.ui.lockView

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mikhaellopez.biometric.BiometricHelper
import com.mikhaellopez.biometric.BiometricPromptInfo
import com.mikhaellopez.biometric.BiometricType
import dagger.hilt.android.AndroidEntryPoint
import dev.skomlach.biometric.compat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.gxteam.variant.ListenerActivity
import uz.gxteam.variant.R
import uz.gxteam.variant.adapters.RvCalckAdapter
import uz.gxteam.variant.databinding.DialogParolBinding
import uz.gxteam.variant.databinding.ErrorDialogBinding
import uz.gxteam.variant.databinding.FragmentLockBinding
import uz.gxteam.variant.errors.errorInternet.errorNoClient
import uz.gxteam.variant.errors.errorInternet.noInternet
import uz.gxteam.variant.resourse.userResourse.UserDataResourse
import uz.gxteam.variant.vm.authViewModel.AuthViewModel
import kotlin.coroutines.CoroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LockFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class LockFragment : Fragment(R.layout.fragment_lock),CoroutineScope {
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
    private val authViewModel: AuthViewModel by viewModels()
    private val binding:FragmentLockBinding by viewBinding()
    lateinit var rvCalckAdapter: RvCalckAdapter
    lateinit var listNumber:ArrayList<String>
    lateinit var listenerActivity:ListenerActivity
    var code:String=""
    var passWordApp:String=""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {



            launch {
                authViewModel.getUserData().collect{
                    when(it){
                        is UserDataResourse.SuccessUserResourse->{
                            authViewModel.getSharedPreference().userData = Gson().toJson(it.userData)
                        }
                        is UserDataResourse.ErrorUserResourse->{
                            if (it.internetConnection==true){
                                if (it.errorCode==401){
                                    var navOpitions = NavOptions.Builder().setPopUpTo(R.id.authFragment,false).build()
                                    var bundle = Bundle()
                                    findNavController().navigate(R.id.authFragment,bundle,navOpitions)
                                }else{
                                    Log.e("Error_Data", it.error.toString())
                                    errorNoClient(requireContext(),it.errorCode?:0)
                                }
                            }else{
                                noInternet(requireContext())
                            }
                        }
                    }
                }
            }



            passWordApp = authViewModel.getSharedPreference().passwordApp.toString()

            if (passWordApp != ""){
                namePage.text =getString(R.string.password_write)
            }

            loadNumber()

            rvCalckAdapter = RvCalckAdapter(object:RvCalckAdapter.OnItemClickListener{
                override fun onItemClick(str: String, position: Int) {
                    if (code.length<=4) {
                        code += str
                        if (code.length==5){
                            if (passWordApp == ""){
                                var dialog = AlertDialog.Builder(requireContext(),R.style.BottomSheetDialogThem)
                                val create = dialog.create()
                                val dialogParolBinding = DialogParolBinding.inflate(LayoutInflater.from(requireContext()), null, false)
                                dialogParolBinding.okBtn.setOnClickListener {
                                    authViewModel.getSharedPreference().passwordApp = code
                                    findNavController().navigate(R.id.action_lockFragment_to_mainFragment)
                                    code=""
                                    create.dismiss()
                                }
                                dialogParolBinding.noBtn.setOnClickListener {
                                    one.isChecked=false
                                    two.isChecked=false
                                    three.isChecked=false
                                    four.isChecked=false
                                    five.isChecked=false
                                    code=""
                                    create.dismiss()
                                }
                                create.setView(dialogParolBinding.root)
                                create.setCancelable(false)
                                create.show()
                            }else{
                                if (code == passWordApp){
                                    listenerActivity.showLoading()
                                    findNavController().navigate(R.id.action_lockFragment_to_mainFragment)
                                }else{
                                    var dialogAlert = AlertDialog.Builder(requireContext(),R.style.BottomSheetDialogThem)
                                    val create = dialogAlert.create()
                                    var errorDialog = ErrorDialogBinding.inflate(LayoutInflater.from(requireContext()),null,false)
                                    errorDialog.errorText.text = getString(R.string.error_password)
                                    errorDialog.okBtn.setOnClickListener {
                                        create.dismiss()
                                    }
                                    create.setView(errorDialog.root)
                                    create.show()
                                }
                            }
                        }
                    }
                    when(code.length){
                            1->{
                                one.isChecked=true
                            }
                            2->{
                                one.isChecked=true
                                two.isChecked=true
                            }
                            3->{
                                one.isChecked=true
                                two.isChecked=true
                                three.isChecked=true
                            }
                            4->{
                                one.isChecked=true
                                two.isChecked=true
                                three.isChecked=true
                                four.isChecked=true
                            }
                            5->{
                                one.isChecked=true
                                two.isChecked=true
                                three.isChecked=true
                                four.isChecked=true
                                five.isChecked=true
                            }
                        }
                }
                override fun onItemClickDelete(position: Int) {
                    if (code.isNotEmpty()){
                        code = code.substring(0,code.length-1)
                            when(code.length){
                                1->{
                                    one.isChecked=true
                                    two.isChecked=false
                                    three.isChecked=false
                                    four.isChecked=false
                                    five.isChecked=false
                                }
                                2->{
                                    one.isChecked=true
                                    two.isChecked=true
                                    three.isChecked=false
                                    four.isChecked=false
                                    five.isChecked=false
                                }
                                3->{
                                    one.isChecked=true
                                    two.isChecked=true
                                    three.isChecked=true
                                    four.isChecked=false
                                    five.isChecked=false
                                }
                                4->{
                                    one.isChecked=true
                                    two.isChecked=true
                                    three.isChecked=true
                                    four.isChecked=true
                                    five.isChecked=false
                                }
                                5->{
                                    one.isChecked=true
                                    two.isChecked=true
                                    three.isChecked=true
                                    four.isChecked=true
                                    five.isChecked=true
                                }
                                0->{
                                    one.isChecked=false
                                }
                            }
                    }
                }

                override fun bioMetrickClick(position: Int) {
                    biometrick()
                }
            },listNumber)
            rvNumber.adapter = rvCalckAdapter
            rvNumber.isNestedScrollingEnabled=false

            biometrick()

            listenerActivity.hideLoading()
        }
    }

    private fun biometrick() {
        if (!authViewModel.getSharedPreference().passwordApp.equals("")){
            val biometricHelper = BiometricHelper(this@LockFragment)
            // BiometricType = FACE, FINGERPRINT, IRIS, MULTIPLE or NONE
            val biometricType: BiometricType = biometricHelper.getBiometricType()
            // Check if biometric is available on the device
            // btnStart.visibility = if (biometricHelper.biometricEnable()) View.VISIBLE else View.GONE
            var promptInfo = BiometricPromptInfo(getString(R.string.fingerPrint),getString(R.string.fingerPrint1), confirmationRequired = true)
            biometricHelper.showBiometricPrompt(promptInfo,
                onError = { errorCode: Int, errString: CharSequence ->
                    // Do something when error

                }, onFailed = {
                    // Do something when failed

                }, onSuccess = { result: BiometricPrompt.AuthenticationResult ->
                    // Do something when success
                    findNavController().navigate(R.id.action_lockFragment_to_mainFragment)
                })
        }
    }


    private fun loadNumber() {
        listNumber = ArrayList()
        listNumber.add("1")
        listNumber.add("2")
        listNumber.add("3")
        listNumber.add("4")
        listNumber.add("5")
        listNumber.add("6")
        listNumber.add("7")
        listNumber.add("8")
        listNumber.add("9")
        listNumber.add("-1")
        listNumber.add("0")
        listNumber.add("-2")
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
         * @return A new instance of fragment LockFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LockFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}