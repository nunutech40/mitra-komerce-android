package id.android.kmabsensi.data.remote.response.komboard

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CekResiRespons (
    @field:SerializedName("code")
    val code: Int? = null,
    @field:SerializedName("status")
    val status: String? = null,
    @field:SerializedName("data")
    val data: List<DataResultResi>?= null
):Parcelable

@Parcelize
data class DataResultResi(
    @field:SerializedName("order_date")
    val orderDate: String? = null,
    @field:SerializedName("customer_name")
    val customerName: String? = null,
    @field:SerializedName("airway_bill")
    val airwayBil: String? = null,
    @field:SerializedName("cnote")
    val cNote: String? = null
):Parcelable
