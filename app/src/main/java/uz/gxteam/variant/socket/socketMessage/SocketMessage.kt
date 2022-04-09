package uz.gxteam.variant.socket.socketMessage

data class SocketMessage(
    val app_id: Int,
    val message: String,
    val token: String,
    val type: Int
)