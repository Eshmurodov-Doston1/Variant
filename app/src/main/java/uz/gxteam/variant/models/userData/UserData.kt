package uz.gxteam.variant.models.userData

data class UserData(
    val birth_date: Any,
    val branch_id: Int,
    val created_at: Any,
    val document_id: Any,
    val email: Any,
    val id: Int,
    val name: String,
    val partner_id: Int,
    val passport_serial: Any,
    val patronym: String,
    val period: List<Int>,
    val phone: String,
    val photo: Any,
    val pinfl: Any,
    val remember_token: Any,
    val role_id: Int,
    val status: String,
    val surname: String,
    val two_factor_enabled: Any,
    val type: String,
    val updated_at: Any
)