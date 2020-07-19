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
    val `data`: List<WorkConfig> = listOf()
)

@Parcelize
data class WorkConfig(
    @SerializedName("key")
    val key: String = "",
    @SerializedName("value")
    val value: String = ""
):Parcelable