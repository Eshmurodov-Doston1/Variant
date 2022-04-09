package uz.gxteam.variant.models.appliction

data class Application(
    val client_id: Int,
    val client_status_name: String,
    val contract_number: String,
    val full_name: String,
    val id: Int,
    val log_user_id: Int,
    val max_level: Int,
    var cards:String,
    var phones:String,
    val photo_status: Int,
    val status_name: String,
    val token: String
)