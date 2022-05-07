package uz.gxteam.variant.errors.authError

import com.google.gson.annotations.SerializedName

data class AuthErrors(
    @SerializedName("errors")
    val errors: List<Error>?=null,
)