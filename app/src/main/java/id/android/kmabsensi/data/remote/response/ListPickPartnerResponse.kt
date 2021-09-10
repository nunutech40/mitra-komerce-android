package id.android.kmabsensi.data.remote.response
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListPickPartnerResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val partners: List<PickPartner> = listOf()
): Parcelable

@Parcelize
data class PickPartner(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("role_name")
    val roleName: String = "",
    @SerializedName("username")
    val username: String = "",
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("email")
    val email: String = "",
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("no_partner")
    val noPartner: String = "",
    @SerializedName("martial_status")
    val martialStatus: Int = 0
): Parcelable
