package id.android.kmabsensi.data.remote.response
import com.google.gson.annotations.SerializedName


data class ListAlphaResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val `data`: List<Alpha> = listOf()
)

data class Alpha(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("full_name")
    val fullName: String = "",
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("office_id")
    val officeId: Int = 0,
    @SerializedName("total_weekdays")
    val totalWeekdays: Int = 0,
    @SerializedName("total_presence")
    val totalPresence: Int = 0,
    @SerializedName("total_holidaye")
    val totalHolidaye: Int = 0,
    @SerializedName("total_alpha")
    val totalAlpha: Int = 0
)