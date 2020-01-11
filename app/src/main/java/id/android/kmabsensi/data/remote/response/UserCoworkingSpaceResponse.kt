package id.android.kmabsensi.data.remote.response

data class UserCoworkDataResponse(
    val code: Int,
    val `data`: List<UserCoworkingSpace>,
    val message: String,
    val status: Boolean
)

data class UserCoworkingSpace(
    val address: String,
    val available_slot: Int,
    val cowork_name: String,
    val cowork_presence: List<CoworkPresence>,
    val description: String,
    val id: Int,
    val lat: String,
    val lng: String,
    val slot: String,
    val status: String
)

data class CoworkPresence(
    val id: Int,
    val user_id: Int,
    val cowork_id: Int,
    val cowork_name: String,
    val check_in_datetime: String,
    val checkout_date_time: String?
)