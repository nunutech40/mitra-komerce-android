package id.android.kmabsensi.data.remote.response

data class BaseResponse(
    val status: Boolean,
    val code: Int?,
    val message: String
)