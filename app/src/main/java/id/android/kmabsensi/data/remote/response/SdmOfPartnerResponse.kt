package id.android.kmabsensi.data.remote.response
import com.google.gson.annotations.SerializedName


data class SdmOfPartnerResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val sdm: List<SimpleSdm> = listOf()
)

data class SimpleSdm(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("email")
    val email: String = ""
)