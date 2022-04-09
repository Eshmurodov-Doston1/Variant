package uz.gxteam.variant.models.uploadPhotos

data class UploadPhotos(
    val client_application_id: Int,
    val created_at: String,
    val file_extension: String,
    val file_link: String,
    val file_name: String,
    val id: Int,
    val type: Int,
    val updated_at: String
)