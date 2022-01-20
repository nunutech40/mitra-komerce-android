package id.android.kmabsensi.data.remote.response.komboard

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BulkResiRespons (
        @SerializedName("status")
        val status: String?= null,
        @SerializedName("code")
        val code: Int? = null,
        @SerializedName("data")
        val data: List<DataBulkResi>? = null
        ):Parcelable

@Parcelize
data class DataBulkResi(
    @SerializedName("airway_bill")
    val airway_bil: String?= null,
    @SerializedName("receiver")
    val reveriver: String? = null,
    @SerializedName("customer")
    val customer: String? = null,
    @SerializedName("cnote_date")
    val cnote_date: String? = null,
    @SerializedName("cnote_last_update")
    val cnote_last_update: String? = null,
    @SerializedName("airway_address")
    val airway_addres: String? = null,
    @SerializedName("last_status")
    val last_status: String? = null
):Parcelable