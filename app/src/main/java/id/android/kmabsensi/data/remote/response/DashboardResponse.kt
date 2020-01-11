package id.android.kmabsensi.data.remote.response

data class DashboardResponse(
    val code: Int,
    val `data`: Dashboard,
    val message: String,
    val status: Boolean
)

data class Dashboard(
    val total_cssr: Int,
    val total_failed_present: Int,
    val total_holiday: Int,
    val total_not_present: Int,
    val total_not_yet_present: Int,
    val total_permission: Int,
    val total_present: Int,
    val total_sick: Int,
    val total_user: Int
)