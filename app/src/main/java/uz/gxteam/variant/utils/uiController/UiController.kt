package uz.gxteam.variant.utils.uiController

interface UiController {
    fun showProgress()
    fun hideProgress()
    fun error(errorCode:Long,errorMessage:String,onClick:(isClick:Boolean)->Unit)
}