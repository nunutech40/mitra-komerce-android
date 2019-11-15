package id.android.kmabsensi.data.remote.response

data class PresenceReportResponse(
    val code: Int,
    val `data`: Data,
    val message: String,
    val status: Boolean
)

data class Data(
    val presence: List<Presence>,
    val report: Report
)

data class Presence(
    val checkIn_photo_url: String,
    val check_in_datetime: String,
    val checkout_date_time: String?,
    val checkOut_photo_url: String?,
    val created_at: String,
    val id: Int,
    val office_id: Int,
    val office_name: String,
    val role_id: Int,
    val updated_at: String,
    val user_id: Int,
    val user: User?,
    val management: User,
    val user_management_id: Int
)

data class Report(
    val percentage: String,
    val total_not_present: Int,
    val total_present: Int,
    val total_user: Int
)