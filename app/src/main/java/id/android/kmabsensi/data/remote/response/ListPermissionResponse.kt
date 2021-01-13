package id.android.kmabsensi.data.remote.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import com.google.gson.annotations.SerializedName

data class ListPermissionResponse(
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("status")
    val status: Boolean = false,
    @SerializedName("message")
    val message: String = "",
    @SerializedName("data")
    val `data`: List<Permission> = listOf()
)

@Parcelize
data class Permission(
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("permission_type")
    val permissionType: Int = 0,
    @SerializedName("user_id")
    val userId: Int = 0,
    @SerializedName("user")
    val user: User = User(),
    @SerializedName("management")
    val management: User? = null,
    @SerializedName("role_id")
    val roleId: Int = 0,
    @SerializedName("user_management_id")
    val userManagementId: Int = 0,
    @SerializedName("attachment_img_url")
    val attachmentImgUrl: String = "",
    @SerializedName("attachment_leader_img_url")
    val attachmentLeaderImgUrl: String = "",
    @SerializedName("attachment_partner_img_url")
    val attachmentPartnerImgUrl: String = "",
    @SerializedName("status")
    val status: Int = 0,
    @SerializedName("explanation")
    val explanation: String = "",
    @SerializedName("date_from")
    val dateFrom: String = "",
    @SerializedName("date_to")
    val dateTo: String = "",
    @SerializedName("created_at")
    val createdAt: String = ""
): Parcelable {
    @Parcelize
    data class User(
        @SerializedName("id")
        val id: Int = 0,
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
        @SerializedName("npk")
        val npk: String = "",
        @SerializedName("division_id")
        val divisionId: Int = 0,
        @SerializedName("division_name")
        val divisionName: String = "",
        @SerializedName("office_id")
        val officeId: Int = 0,
        @SerializedName("office_name")
        val officeName: String = "",
        @SerializedName("position_id")
        val positionId: Int = 0,
        @SerializedName("position_name")
        val positionName: String = "",
        @SerializedName("origin_village")
        val originVillage: String = "",
        @SerializedName("no_hp")
        val noHp: String = "",
        @SerializedName("address")
        val address: String = "",
        @SerializedName("join_date")
        val joinDate: String = "",
        @SerializedName("martial_status")
        val martialStatus: Int = 0,
        @SerializedName("photo_profile_url")
        val photoProfileUrl: String = "",
        @SerializedName("birth_date")
        val birthDate: String = "",
        @SerializedName("gender")
        val gender: Int = 0,
        @SerializedName("last_date_of_pause")
        val lastDateOfPause: String = "",
        @SerializedName("kmpoin")
        val kmpoin: Int = 0,
        @SerializedName("user_management_id")
        val userManagementId: Int = 0
    ): Parcelable
}

