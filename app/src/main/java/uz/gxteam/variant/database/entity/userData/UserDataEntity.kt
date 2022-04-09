package uz.gxteam.variant.database.entity.userData

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class UserDataEntity(
    val birth_date: String,
    val branch_id: Int,
    val created_at: Int,
    val document_id: String,
    val email: String,
    @PrimaryKey
    val id: Int,
    val name: String,
    val partner_id: Int,
    val passport_serial: String,
    val patronym: String,
    val period: String,
    val phone: String,
    val photo: String,
    val pinfl: String,
    val remember_token: String,
    val role_id: Int,
    val status: String,
    val surname: String,
    val two_factor_enabled: Int,
    val type: String,
    val updated_at: Int
)