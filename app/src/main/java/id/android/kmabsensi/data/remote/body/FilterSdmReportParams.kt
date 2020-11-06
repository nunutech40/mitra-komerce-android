package id.android.kmabsensi.data.remote.body

data class FilterSdmReportParams(
    val user_id: Int,
    val user_management_id: Int,
    val no_partner: Int,
    val start_date: String,
    val end_date: String
)