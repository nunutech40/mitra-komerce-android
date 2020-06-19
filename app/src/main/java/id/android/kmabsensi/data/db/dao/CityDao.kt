package id.android.kmabsensi.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import id.android.kmabsensi.data.db.entity.City
import io.reactivex.Single

@Dao
interface CityDao: BaseDao<City> {

    @Query("SELECT * FROM city WHERE mstKodeWilayah = :kodeWilayah")
    fun findByKodeWilayah(kodeWilayah: String): Single<List<City>>

}