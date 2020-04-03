package id.android.kmabsensi.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import id.android.kmabsensi.data.db.dao.CityDao
import id.android.kmabsensi.data.db.dao.ProvinceDao
import id.android.kmabsensi.data.db.entity.City
import id.android.kmabsensi.data.db.entity.Province

@Database(
    entities = [Province::class, City::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase(){

    abstract fun provinceDao() : ProvinceDao
    abstract fun cityDao() : CityDao

}