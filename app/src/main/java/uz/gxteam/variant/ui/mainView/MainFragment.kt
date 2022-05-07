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

}