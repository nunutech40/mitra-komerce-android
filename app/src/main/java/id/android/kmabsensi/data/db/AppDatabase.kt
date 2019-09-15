package id.android.kmabsensi.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import id.android.kmabsensi.data.db.dao.TeamDao
import id.android.kmabsensi.data.db.entity.TeamEntity

@Database(
    entities = [TeamEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun teamDao() : TeamDao

}