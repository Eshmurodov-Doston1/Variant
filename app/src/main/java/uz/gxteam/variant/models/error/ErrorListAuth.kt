package uz.gxteam.variant.models.error

import com.google.gson.annotations.SerializedName
import uz.gxteam.variant.errors.authError.Error

data class ErrorListAuth(
    @SerializedName("errors")
    val errors:List<Error>
)
