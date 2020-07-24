package id.android.kmabsensi.data.remote.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class ListPositionResponse(
    val code: Int,
    val `data`: List<Position>,
    val message: String,
    val status: Boolean
)

@Parcelize
data class Position(
    val id: Int,
    val position_name: String,
    var isChecked: Boolean = false
): Parcelable

