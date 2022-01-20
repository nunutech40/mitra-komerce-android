package id.android.kmabsensi.data.remote.response.komboard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class OngkirRespons (
    @field:SerializedName("rajaongkir")
    val rajaongkirOngkir: ResponsOngkirResults? = null
): Parcelable
@Parcelize
data class ResponsOngkirResults (
    @field:SerializedName("query")
    val queryOngkir: QueryOngkirResults? = null,
    @field:SerializedName("status")
    val statusOngkir: StatusOngkirResults? = null,
    @field:SerializedName("origin_details")
    val originDetails: OriginDetails?= null,
    @field: SerializedName("destination_details")
    val destinationDetails: DestinationDetails? = null,
    @field:SerializedName("results")
    val resultsOngkir: List<OngkirResults>? = null
): Parcelable

@Parcelize
data class QueryOngkirResults(
    @field:SerializedName("key")
    val key: String? = null
): Parcelable

@Parcelize
data class StatusOngkirResults(
    @field:SerializedName("code")
    val code: Int? = null,
    @field:SerializedName("description")
    val description: String? = null
): Parcelable

@Parcelize
data class OriginDetails(
    @field:SerializedName("subdistrict_id")
    val subdistrcitIDOrigin: Int? = null,
    @field:SerializedName("province_id")
    val provinceIDOrigin: Int? = null,
    @field:SerializedName("province")
    val provinceOrigin: String? = null,
    @field:SerializedName("city_id")
    val cityIDOrigin: Int? = null,
    @field:SerializedName("city")
    val cityOrigin: String? = null,
    @field:SerializedName("type")
    val typeOrigin: String? = null,
    @field:SerializedName("subdistrict_name")
    val subdistrictOrigin: String? = null
): Parcelable

@Parcelize
data class DestinationDetails(
    @field:SerializedName("subdistrict_id")
    val subdistrcitIDDestination: Int? = null,
    @field:SerializedName("province_id")
    val provinceIDDestination: Int? = null,
    @field:SerializedName("province")
    val provinceDestination: String? = null,
    @field:SerializedName("city_id")
    val cityIDDestination: Int? = null,
    @field:SerializedName("city")
    val cityDestination: String? = null,
    @field:SerializedName("type")
    val typeDestination: String? = null,
    @field:SerializedName("subdistrict_name")
    val subdistrictDestination: String? = null
): Parcelable

@Parcelize
data class OngkirResults(
    @field:SerializedName("code")
    val codeOngkir: String? = null,
    @field:SerializedName("name")
    val nameOngkir: String?= null,
    @field:SerializedName("costs")
    val costsData: List<CostResults>? = null
): Parcelable

@Parcelize
data class CostResults(
    @field:SerializedName("service")
    val serviceCost: String?= null,
    @field:SerializedName("description")
    val descriptionCost:String? = null,
    @field:SerializedName("cost")
    val cost: List<CostItem>? = null
):Parcelable


@Parcelize
data class CostItem(
    @field:SerializedName("value")
    val valueCost:Int? = null,
    @field:SerializedName("etd")
    val etdCost: String? = null,
    @field:SerializedName("note")
    val noteCost: String? = null
): Parcelable