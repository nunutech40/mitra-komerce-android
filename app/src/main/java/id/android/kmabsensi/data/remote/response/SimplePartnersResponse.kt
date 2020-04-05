package id.android.kmabsensi.data.remote.response
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class SimplePartnersResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("data")
    val partners: List<SimplePartner> = listOf()
)

@Parcelize
data class SimplePartner(
    @SerializedName("no_partner")
    val noPartner: String = "",
    @SerializedName("full_name")
    val fullName: String = ""
): Parcelable