package id.android.kmabsensi.data.remote.body

data class ListAlphaParams(
    val role_id: Int,
    val user_management_id: Int,
    val no_partner: String,
    val office_id: Int,
    val start_date: String,
    val end_date: String
)