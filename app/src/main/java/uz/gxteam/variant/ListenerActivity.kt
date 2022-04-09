package uz.gxteam.variant

import uz.gxteam.variant.models.getApplications.DataApplication

interface ListenerActivity {
    fun showLoading()
    fun hideLoading()
    fun createStateMentView(dataApplication: DataApplication)
}