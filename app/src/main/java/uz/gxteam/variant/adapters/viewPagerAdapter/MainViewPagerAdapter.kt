package uz.gxteam.variant.adapters.viewPagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.gxteam.variant.ui.mainView.view.chat.ChatListFragment
import uz.gxteam.variant.ui.mainView.view.settings.SettingsFragment
import uz.gxteam.variant.ui.mainView.view.statement.StateMentFragment
import uz.gxteam.variant.utils.AppConstant.ONE
import uz.gxteam.variant.utils.AppConstant.THREE
import uz.gxteam.variant.utils.AppConstant.TWO
import uz.gxteam.variant.utils.AppConstant.ZERO

class MainViewPagerAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return THREE
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            ZERO->{
                StateMentFragment()
            }
            ONE->{
                ChatListFragment()
            }
            TWO->{
                SettingsFragment()
            }
            else->{
                StateMentFragment()
            }
        }
    }
}