package id.android.kmabsensi.data.remote.response


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class UserConfigurationResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("data")
    val data: List<User> = mutableListOf(),
    @SerializedName("message")
    val message: String = "",
    @SerializedName("status")
    val status: Boolean = false
)


@Parcelize
data class SdmConfig(
    @SerializedName("shift_mode")
    val shiftMode: String = "",
    @SerializedName("user_id")
    val userId: String = "",
    @SerializedName("id")
    val id: Int = 0
): Parcelable


