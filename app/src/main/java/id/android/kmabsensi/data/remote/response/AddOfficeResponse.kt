package id.android.kmabsensi.data.remote.response

data class CrudOfficeResponse(
    val code: Int,
    val `data`: OfficeData,
    val message: String,
    val status: Boolean
)

data class OfficeData(
    val id: Int,
    val address: String,
    val lat: Double,
    val lng: Double,
    val office_name: String,
    val pj_user_id: Int
)