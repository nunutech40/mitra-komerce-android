package id.android.kmabsensi.data.remote.response.komboard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class SubDistrictRespons(
    @field:SerializedName("rajaongkir")
    val rajaongkirSubdistrict: ResponsSubDistrictResults? = null
): Parcelable
@Parcelize
data class ResponsSubDistrictResults (
    @field:SerializedName("query")
    val querySubdistrict: QuerySubdistrictResults? = null,
    @field:SerializedName("status")
    val statusSubdistrict: StatusSubDistrictResults? = null,
    @field:SerializedName("results")
    val resultsSubdistrict: List<SubDistrictResults>? = null
): Parcelable

@Parcelize
data class QuerySubdistrictResults(
    @field:SerializedName("key")
    val key: String? = null
): Parcelable

@Parcelize
data class StatusSubDistrictResults(
    @field:SerializedName("code")
    val code: Int? = null,
    @field:SerializedName("description")
    val description: String? = null
): Parcelable

@Parcelize
data class SubDistrictResults(
    @field:SerializedName("subdistrict_id")
    val subdistrict_id: Int? = null,
    @field:SerializedName("province_id")
    val province_id: Int? = null,
    @field:SerializedName("province")
    val province_name: String? = null,
    @field:SerializedName("city_id")
    val city_id: Int? = null,
    @field:SerializedName("city")
    val city: String? = null,
    @field:SerializedName("type")
    val type:String? = null,
    @field:SerializedName("subdistrict_name")
    val subdistrict_name:String? = null
): Parcelable
