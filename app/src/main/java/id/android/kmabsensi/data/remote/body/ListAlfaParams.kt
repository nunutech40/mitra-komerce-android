package id.android.kmabsensi.data.remote.body

data class ListAlphaParams(
    val role_id: Int = 0,
    val user_management_id: Int = 0,
    val no_partner: String = "0",
    val office_id: Int = 0,
    val start_date: String = "",
    val end_date: String = ""
)