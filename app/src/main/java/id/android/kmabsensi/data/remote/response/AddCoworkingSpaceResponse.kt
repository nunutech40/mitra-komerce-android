package id.android.kmabsensi.data.remote.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class AddCoworkingSpaceResponse(
    val code: Int,
    val `data`: CoworkingSpace,
    val message: String,
    val status: Boolean
)

@Parcelize
data class CoworkingSpace(
    val id: Int,
    val address: String,
    val cowork_name: String,
    val description: String,
    val lat: Double,
    val lng: Double,
    val slot: Int,
    val status: Int
): Parcelable