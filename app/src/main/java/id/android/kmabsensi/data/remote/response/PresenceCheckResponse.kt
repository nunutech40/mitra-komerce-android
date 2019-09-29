package id.android.kmabsensi.data.remote.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class PresenceCheckResponse(
    val checkdeIn: Boolean,
    val code: Int,
    val office_assigned: OfficeAssigned,
    val presence_id: Int,
    val status: Boolean
)

@Parcelize
data class OfficeAssigned(
    val address: String,
    val created_at: String,
    val id: Int,
    val lat: String,
    val lng: String,
    val office_name: String,
    val pj_user_id: String,
    val updated_at: String
) : Parcelable