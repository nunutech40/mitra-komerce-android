package id.android.kmabsensi.data.remote.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class ListPermissionResponse(
    val code: Int,
    val `data`: List<Permission>,
    val message: String,
    val status: Boolean
)

@Parcelize
data class Permission(
    val attachment_img_url: String,
    val date_from: String,
    val date_to: String,
    val explanation: String,
    val id: Int,
    val management: User?,
    val permission_type: Int,
    val role_id: Int,
    val status: Int,
    val user: User,
    val user_id: Int,
    val user_management_id: Int
) : Parcelable
