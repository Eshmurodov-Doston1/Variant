package uz.gxteam.variant.models.messages.resMessage

data class Message(
    val application_id: Int,
    val id: Int,
    val message: String,
    val photo: Any?=null,
    val photo_id: Any,
    val type: Int,
    val user_id: Int
)