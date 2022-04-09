package uz.gxteam.variant.models.getApplications

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataApplication(
    @SerializedName("status")
    val status: Int?=null,
    @SerializedName("level")
    var level:Long?=null,
    @SerializedName("client_id")
    var client_id:Long?=null,
    @SerializedName("contract_number")
    var contract_number:String?=null,
    @SerializedName("photo_status")
    var photo_status:Long?=null,
    @SerializedName("token")
    val token: String,
    @SerializedName("status_title")
    var status_title:String?=null,
    @SerializedName("full_name")
    var full_name:String?=null
):Serializable