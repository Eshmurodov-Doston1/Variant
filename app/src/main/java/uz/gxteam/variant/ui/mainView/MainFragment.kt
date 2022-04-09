package uz.gxteam.variant.ui.mainView

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import uz.gxteam.variant.ListenerActivity
import uz.gxteam.variant.R
import uz.gxteam.variant.adapters.viewPagerAdapter.MainViewPagerAdapter
import uz.gxteam.variant.databinding.FragmentMainBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment(R.layout.fragment_main) {
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
    private val binding:FragmentMainBinding by viewBinding()
    lateinit var listenerActivity:ListenerActivity
    lateinit var mainViewPagerAdapter:MainViewPagerAdapter
    var isViewCreate:Boolean?=false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            listenerActivity.hideLoading()
            isViewCreate = true

            viewPager2.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when(position){
                        0->{
                            bottomNavigation.menu.findItem(R.id.home).isChecked=true
                        }
                        1->{
                            bottomNavigation.menu.findItem(R.id.chat).isChecked=true
                        }
                        2->{
                            bottomNavigation.menu.findItem(R.id.settings).isChecked=true
                        }
                    }
                }
            })
            bottomNavigation.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.home->{
                        viewPager2.currentItem=0
                    }
                    R.id.chat->{
                        viewPager2.currentItem=1
                    }
                    R.id.settings->{
                        viewPager2.currentItem=2
                    }
                }
                true
            }

            mainViewPagerAdapter = MainViewPagerAdapter(requireActivity())
            viewPager2.adapter = mainViewPagerAdapter

        }
    }





    override fun onDestroy() {
        super.onDestroy()
        isViewCreate = false
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
         * @return A new instance of fragment MainFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}