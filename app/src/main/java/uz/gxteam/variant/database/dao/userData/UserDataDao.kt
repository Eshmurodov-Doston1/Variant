package uz.gxteam.variant.database.dao.userData

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import uz.gxteam.variant.database.entity.userData.UserDataEntity

@Dao
interface UserDataDao {
    @Insert
    suspend fun saveUserData(userDataEntity: UserDataEntity)

    @Query("DELETE FROM userdataentity")
    suspend fun deleteUserDataTable()

    @Query("SELECT*FROM userdataentity")
    fun getUserData():UserDataEntity


    @Query("SELECT*FROM userdataentity")
    fun getUserDataList():List<UserDataEntity>
}