package id.android.kmabsensi.data.remote.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OfficeResponse(
    val code: Int,
    val `data`: List<Office>,
    val message: String,
    val status: Boolean
): Parcelable

@Parcelize
data class Office(
    val address: String,
    val created_at: String,
    val id: Int,
    val lat: Double,
    val lng: Double,
    val office_name: String,
    val pj_user_id: Int,
    val updated_at: String
) : Parcelable