package uz.gxteam.variant.ui.mainView

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import uz.gxteam.variant.R
import uz.gxteam.variant.adapters.viewPagerAdapter.MainViewPagerAdapter
import uz.gxteam.variant.databinding.FragmentMainBinding
import uz.gxteam.variant.ui.baseFragment.BaseFragment
import uz.gxteam.variant.utils.AppConstant.ONE
import uz.gxteam.variant.utils.AppConstant.TWO
import uz.gxteam.variant.utils.AppConstant.ZERO

@AndroidEntryPoint
class MainFragment : BaseFragment(R.layout.fragment_main) {
    private val binding:FragmentMainBinding by viewBinding()
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
                        ZERO->{
                            bottomNavigation.menu.findItem(R.id.home).isChecked=true
                        }
                        ONE->{
                            bottomNavigation.menu.findItem(R.id.chat).isChecked=true
                        }
                        TWO->{
                            bottomNavigation.menu.findItem(R.id.settings).isChecked=true
                        }
                    }
                }
            })
            bottomNavigation.setOnItemSelectedListener {
                when(it.itemId){
                    R.id.home->{
                        viewPager2.currentItem= ZERO
                    }
                    R.id.chat->{
                        viewPager2.currentItem= ONE
                    }
                    R.id.settings->{
                        viewPager2.currentItem= TWO
                    }
                }
                true
            }
            mainViewPagerAdapter = MainViewPagerAdapter(requireActivity())
            viewPager2.adapter = mainViewPagerAdapter

        }
    }

}