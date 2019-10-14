package id.android.kmabsensi.data.remote.response

data class PresenceHistoryResponse(
    val code: Int,
    val `data`: List<PresenceHistory>,
    val message: String
)

data class PresenceHistory(
    val checkIn_photo_url: String,
    val checkOut_photo_url: String?,
    val check_in_datetime: String,
    val checkout_date_time: String?,
    val created_at: String,
    val id: Int,
    val office_id: Int,
    val office_name: String,
    val role_id: Int,
    val updated_at: String,
    val user_id: Int,
    val user_management_id: Int,
    val user: User?
)