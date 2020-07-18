package id.android.kmabsensi.data.remote.response
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class WorkConfigResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val `data`: WorkConfig = WorkConfig()
)

@Parcelize
data class WorkConfig(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("work_mode")
    val workMode: String = "",
    @SerializedName("work_scope")
    val workScope: String = "",
    @SerializedName("wfh_start_date")
    val wfhStartDate: String = "",
    @SerializedName("wfh_end_date")
    val wfhEndDate: String = "",
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String = ""
):Parcelable