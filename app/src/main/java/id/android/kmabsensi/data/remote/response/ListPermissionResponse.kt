package id.android.kmabsensi.data.remote.response

data class ListPermissionResponse(
    val code: Int,
    val `data`: List<Permission>,
    val message: String,
    val status: Boolean
)

data class Permission(
    val attachment_img_url: String,
    val explanation: String,
    val id: Int,
    val office_id: Int,
    val permission_type: Int,
    val role_id: Int,
    val status: Int,
    val user_id: Int,
    val user_management_id: Int,
    val date_from: String,
    val date_to: String
)