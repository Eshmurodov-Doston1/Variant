package uz.gxteam.variant.ui.baseFragment

import android.content.Context
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import uz.gxteam.variant.ListenerActivity
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment(layout_id:Int) : Fragment(layout_id),CoroutineScope {
    lateinit var listenerActivity:ListenerActivity
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listenerActivity = activity as ListenerActivity
    }
}