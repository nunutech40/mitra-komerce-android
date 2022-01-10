package id.android.kmabsensi.data.remote.response.komboard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CityRespons(
    @field:SerializedName("rajaongkir")
    val rajaongkirCity: ResponsCityResults? = null
): Parcelable
@Parcelize
data class ResponsCityResults (
    @field:SerializedName("query")
    val queryCity: QueryCityResults? = null,
    @field:SerializedName("status")
    val statusCity: StatusCityResults? = null,
    @field:SerializedName("results")
    val resultsCity: List<CityResults>? = null
): Parcelable

@Parcelize
data class QueryCityResults(
    @field:SerializedName("key")
    val key: String? = null
): Parcelable

@Parcelize
data class StatusCityResults(
    @field:SerializedName("code")
    val code: Int? = null,
    @field:SerializedName("description")
    val description: String? = null
): Parcelable

@Parcelize
data class CityResults(
    @field:SerializedName("city_id")
    val city_id: Int? = null,
    @field:SerializedName("province_id")
    val province_id: Int? = null,
    @field:SerializedName("province")
    val province_name: String? = null,
    @field:SerializedName("type")
    val type: String? = null,
    @field:SerializedName("city_name")
    val city_name: String? = null,
    @field:SerializedName("postal_code")
    val postal_code:Int? = null
): Parcelable
