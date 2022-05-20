package uz.gxteam.variant.errors.authError

import com.google.gson.annotations.SerializedName
import uz.gxteam.variant.utils.AppConstant.AUTHERRORFIELD

data class AuthErrors(
    @SerializedName(AUTHERRORFIELD)
    val errors: List<Error>?=null,
)