package uz.gxteam.variant.models.messages.resMessage

data class Photo(
    val chat_application_id: Int,
    val created_at: Any,
    val file_extension: String,
    val file_link: String,
    val file_name: String,
    val id: Int,
    val type: Int,
    val updated_at: Any
)