package uz.gxteam.variant.vm.viewVm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skydoves.colorpickerview.ColorEnvelope
import dagger.hilt.android.lifecycle.HiltViewModel
import uz.gxteam.variant.interceptor.MySharedPreference
import uz.gxteam.variant.interceptor.MySharedPreference_Factory
import javax.inject.Inject

@HiltViewModel
class ViewViewModel @Inject constructor(
    private val mySharedPreference: MySharedPreference
):ViewModel() {
    var theme = MutableLiveData<String>()
    fun getThemeLive():LiveData<String>{
        theme.postValue(mySharedPreference.theme_color)
        return theme
    }

    fun setThemeLive(){
        theme.postValue(mySharedPreference.theme_color)
    }


}