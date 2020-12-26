package id.android.kmabsensi.data.remote.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class UserCoworkDataResponse(
    val code: Int,
    val `data`: List<UserCoworkingSpace>,
    val message: String,
    val status: Boolean
)

@Parcelize
data class UserCoworkingSpace(
    val address: String = "",
    val available_slot: Int = 0,
    val cowork_name: String = "",
    val cowork_presence: List<CoworkPresence> = listOf(),
    val description: String = "",
    val id: Int = 0,
    val lat: String = "",
    val lng: String = "",
    val slot: String = "",
    val status: String = ""
): Parcelable

@Parcelize
data class CoworkPresence(
    val id: Int,
    val user_id: Int,
    val cowork_id: Int,
    val cowork_name: String,
    val check_in_datetime: String,
    val checkout_date_time: String?
): Parcelable