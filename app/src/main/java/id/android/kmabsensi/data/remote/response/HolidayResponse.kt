package id.android.kmabsensi.data.remote.response
import com.google.gson.annotations.SerializedName


data class HolidayResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val `data`: List<Holiday> = listOf()
)

data class Holiday(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("event_name")
    val eventName: String = "",
    @SerializedName("start_date")
    val startDate: String = "",
    @SerializedName("end_date")
    val endDate: String = "",
    @SerializedName("notes")
    val notes: Any? = null,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = ""
)