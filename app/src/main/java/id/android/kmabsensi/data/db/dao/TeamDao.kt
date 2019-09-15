package id.android.kmabsensi.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Single
import id.android.kmabsensi.data.db.entity.TeamEntity

@Dao
interface TeamDao : BaseDao<TeamEntity> {

    @Query("SELECT * FROM team")
    fun findAll() : Single<List<TeamEntity>>

}