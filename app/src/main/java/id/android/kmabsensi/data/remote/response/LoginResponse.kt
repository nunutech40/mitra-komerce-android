package id.android.kmabsensi.data.remote.response

data class LoginResponse(
    val access_token: String,
    val expires_at: String,
    val token_type: String,
    val user_id: Int,
    val status: String?,
    val message: String?
)