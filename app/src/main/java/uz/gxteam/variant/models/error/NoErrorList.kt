package uz.gxteam.variant.models.error

import com.google.gson.annotations.SerializedName
import uz.gxteam.variant.errors.authError.Error

data class NoErrorList(
    @SerializedName("errors")
    var errors:Error
)

