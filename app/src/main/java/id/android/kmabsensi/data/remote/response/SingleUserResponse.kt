package id.android.kmabsensi.data.remote.response

data class SingleUserResponse(
    val status: Boolean,
    val `data`: User,
    val message: String
)
