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

data class MenuRoleByPositionResponse(
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val `data`: List<RolePosition> = listOf()
)

data class RolePosition(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("position_name")
    val positionName: String = "",
    @SerializedName("menus")
    val menus: List<Menu> = listOf()
)

data class Menu(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("name")
    val name: String = "",
    @SerializedName("desc")
    val desc: String = ""
)

