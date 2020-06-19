package id.android.kmabsensi.data.remote.response

import com.google.gson.annotations.SerializedName
import id.android.kmabsensi.data.db.entity.City
import id.android.kmabsensi.data.db.entity.Province

class ListAreaResponse(
    val status: Boolean,
    val code: Int,
    val data: DataAreaResponse
)


data class DataAreaResponse(
    val cities: List<City>,
    val provinces: List<Province>
)