package id.android.kmabsensi.data.remote.response.komboard

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ProvinceRespons(
    @field:SerializedName("rajaongkir")
    val rajaongkir: RajaOngkirResults? = null
): Parcelable
@Parcelize
data class RajaOngkirResults (
    @field:SerializedName("query")
    val query: QueryResults? = null,
    @field:SerializedName("status")
    val status: StatusResults? = null,
    @field:SerializedName("results")
    val results: List<ProvinceResults>? = null
): Parcelable

@Parcelize
data class QueryResults(
    @field:SerializedName("key")
    val key: String? = null
): Parcelable

@Parcelize
data class StatusResults(
    @field:SerializedName("code")
    val code: Int? = null,
    @field:SerializedName("description")
    val description: String? = null
): Parcelable

@Parcelize
data class ProvinceResults(
    @field:SerializedName("province_id")
    val province_id: Int? = null,
    @field:SerializedName("province")
    val province_name: String? = null
): Parcelable
