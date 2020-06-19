package id.android.kmabsensi.data.remote.response

data class ListCoworkingSpaceResponse(
    val code: Int,
    val `data`: List<CoworkingSpace>,
    val message: String,
    val status: Boolean
)