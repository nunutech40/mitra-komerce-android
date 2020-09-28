package id.android.kmabsensi.data.remote.body

data class EditHolidayParams(
    val id: Int,
    val event_name: String,
    val start_date: String,
    val end_date: String,
    val notes: String
)