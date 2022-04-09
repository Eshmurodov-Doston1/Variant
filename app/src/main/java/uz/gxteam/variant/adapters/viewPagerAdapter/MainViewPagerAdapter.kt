package uz.gxteam.variant.adapters.viewPagerAdapter

import android.provider.Settings
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.gxteam.variant.ui.mainView.view.chat.ChatListFragment
import uz.gxteam.variant.ui.mainView.view.settings.SettingsFragment
import uz.gxteam.variant.ui.mainView.view.statement.StateMentFragment

class MainViewPagerAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                StateMentFragment()
            }
            1->{
                ChatListFragment()
            }
            2->{
                SettingsFragment()
            }
            else->{
                StateMentFragment()
            }
        }
    }
}