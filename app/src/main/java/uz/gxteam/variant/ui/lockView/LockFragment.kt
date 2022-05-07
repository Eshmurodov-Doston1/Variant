package uz.gxteam.variant.ui.lockView

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.mikhaellopez.biometric.BiometricHelper
import com.mikhaellopez.biometric.BiometricPromptInfo
import com.mikhaellopez.biometric.BiometricType
import dagger.hilt.android.AndroidEntryPoint
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
import uz.gxteam.variant.ui.baseFragment.BaseFragment
import uz.gxteam.variant.vm.authViewModel.AuthViewModel
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class LockFragment : BaseFragment(R.layout.fragment_lock) {

    private val authViewModel: AuthViewModel by viewModels()
    private val binding:FragmentLockBinding by viewBinding()
    lateinit var rvCalckAdapter: RvCalckAdapter
    lateinit var listNumber:ArrayList<String>
    var code:String=""
    var passWordApp:String=""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {



            launch(Dispatchers.Main) {
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
}