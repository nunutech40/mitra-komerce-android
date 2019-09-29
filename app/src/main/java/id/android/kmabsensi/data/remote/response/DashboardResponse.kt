package id.android.kmabsensi.data.remote.response

data class DashboardResponse(
    val code: Int,
    val `data`: Dashboard,
    val message: String,
    val status: Boolean
)

data class Dashboard(
    val total_not_present: Int,
    val total_present: Int,
    val total_user: Int
)