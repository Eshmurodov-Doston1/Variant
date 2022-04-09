package uz.gxteam.variant.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.gxteam.variant.database.dao.userData.UserDataDao
import uz.gxteam.variant.database.entity.userData.UserDataEntity

@Database(entities = [UserDataEntity::class], version = 2)
abstract class AppDatabase :RoomDatabase(){
    abstract fun userDao():UserDataDao
}