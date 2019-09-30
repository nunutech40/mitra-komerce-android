package id.android.kmabsensi.data.remote.response

data class CheckinResponse(
    val code: Int,
    val `data`: Int,
    val message: String,
    val status: Boolean
)