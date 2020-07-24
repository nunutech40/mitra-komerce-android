package id.android.kmabsensi.data.remote.response
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


data class MenuRoleResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val menuRoles: List<Role> = listOf()
)

@Parcelize
data class Role(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("desc")
    val desc: String = "",
    @SerializedName("positions")
    val positions: List<Position> = listOf()
): Parcelable

