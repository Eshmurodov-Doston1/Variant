package uz.gxteam.variant.resourse.uploadPhotos

import uz.gxteam.variant.models.auth.resAuth.ResAuth
import uz.gxteam.variant.models.getApplications.Applications
import uz.gxteam.variant.models.uploadPhotos.UploadPhotos
import uz.gxteam.variant.resourse.authResourse.AuthResourse

sealed class UploadphotosResourse{
    object Loading: UploadphotosResourse()
    data class SuccessUploadPhotos(var uploadPhotos:List<UploadPhotos>?): UploadphotosResourse()
    data class ErrorUploadPhotos(var error:String?=null,var internetConnection:Boolean?=false,var errorCode:Int?=null): UploadphotosResourse()
}