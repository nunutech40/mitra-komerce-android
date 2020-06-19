package id.android.kmabsensi.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import id.android.kmabsensi.data.db.entity.Province
import io.reactivex.Single

@Dao
interface ProvinceDao: BaseDao<Province> {

    @Query("SELECT * FROM province")
    fun findAll(): Single<List<Province>>

}